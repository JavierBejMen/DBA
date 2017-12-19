/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gugelcar;

import JSON.JSON;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.ArrayList;

/**
 *
 * @author Javier Bejar Mendez
 */
public class AgenteVehiculo extends SingleAgent{
    
    private AgentID agente_mapa_id; //ID del agente mapa
    private AgentID controlador_id; //ID del controador del servidor
    //Puede ser que necesitemos los ID de los demás agentes vehiculos, el martes lo resolvemos
    
    private String conversation_id; //ID de la conversación de la sesión
    private String replyWith;
    
    //Atributos propios de cada agente
    private AgentType tipo;
    private int bateria;
    private int fuelrate;
    private int range;
    private boolean fly;
    private boolean objetivo_encontrado;
    private boolean estoy_en_objetivo;
    private Posicion serverPos; //No tengo claro si tenemos distinción entre internalPos y serverPos, el martes lo resolvemos.
    
    //Comunicacion
    private JSON jsonobj;

    /**
     * Define el tipo del agente según los parámetros recibidos por el servidor,
     * puede ser Car, Truck o Drone.
     * @author Javier Bejar Mendez
     */
    private void setType(){
        if(this.fly)
            this.tipo = AgentType.Drone;
        else if(this.fuelrate == 4)
            this.tipo = AgentType.Truck;
        else
            this.tipo = AgentType.Car;
    }
    
    /**
     * Constructor
     * @author Javier Bejar Mendez
     */
    public AgenteVehiculo(AgentID aid, AgentID mapaid, AgentID controlid) throws Exception{
        super(aid);
        this.agente_mapa_id = mapaid;
        this.controlador_id = controlid;
        jsonobj = new JSON();
    }
    
    /**
     * Recibe el conversation ID y si el objetivo se ha encontrado al Agente Mapa 
     * y los guarda en el estado interno.
     * @author Dani
     * 
     */
    private void getConversationID() {
        try {
            ACLMessage inbox = receiveACLMessage();
            objetivo_encontrado = jsonobj.decodeObjetivoEncontrado(inbox.getContent());
            conversation_id = inbox.getConversationId();
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException en getConversationID()."
                    + " Error: " + ex.getMessage());
        }
    }
    
    /**
     * Crea un mensaje con los parámetros especificados.
     * @param sender
     * @param receiver
     * @param performative
     * @param content
     * @param conv_id
     * @param in_reply_to
     * @return ACLMessage con los parámetros especificados
     * @author Dani
     */
    private ACLMessage crearMensaje(AgentID sender, AgentID receiver, int performative,
            String content, String conv_id, String in_reply_to){
        
        ACLMessage outbox = new ACLMessage(performative);
        outbox.setSender(sender);
        outbox.setReceiver(receiver);
        outbox.setContent(content);
        outbox.setConversationId(conv_id);
        outbox.setInReplyTo(in_reply_to);
        
        return outbox;
    }
    
    /**
     * Realiza el request "checkin" para obtener las capabilities del agente.
     * Cuando termina el checkin, envía la confirmación al Agente Mapa.
     * @author Javier Bejar Mendez, Dani
     */
    private void checkin(){
        ACLMessage outbox = crearMensaje(getAid(),controlador_id,ACLMessage.REQUEST,
                    jsonobj.encodeCheckin(), conversation_id, "");
        send(outbox);

        try {
            ACLMessage inbox = receiveACLMessage();
            if(inbox.getPerformativeInt() == ACLMessage.INFORM){
                ArrayList<Object> capabilities = jsonobj.decodeCapabilities(inbox.getContent());
                fuelrate = (int)capabilities.get(0);
                range = (int)capabilities.get(1);
                fly = (boolean)capabilities.get(2);
                replyWith = inbox.getReplyWith();
                setType();
                
                System.out.print("Agente "+getAid().getLocalName()+" Se ha suscrito con éxito con los siguientes parámetros:"
                        + "\nfuelrate: "+fuelrate+"\nrange: "+range+"\nfly: "+"\ntipo: "+tipo + "\n");
                
                ACLMessage outbox2 = crearMensaje(getAid(),agente_mapa_id,ACLMessage.REQUEST,
                    jsonobj.encodeConfirmacionCheckin(), "", "");
                send(outbox2);
                
            } else
                System.out.println("Fallo en checkin(). Details: "+jsonobj.decodeError(inbox.getPerformative()));
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException en checkin(). Error: "+ex.getMessage());
        }  
    }
    
    /**
     * Notifica al agente mapa los parámetros recibidos por el controlador.
     * Dani: ¿Tal vez no es necesario?
     * @author Javier Bejar Mendez
     */
    private void notifyParam(){
        
        ACLMessage outbox = new ACLMessage();
        outbox.setSender(getAid());
        outbox.setReceiver(this.agente_mapa_id);
        outbox.setConversationId("notifyParam");
        outbox.setContent(jsonobj.encodeAgentParam(this));
        outbox.setPerformative(ACLMessage.INFORM);
        
        send(outbox);
    }
    
    /**
     * Realiza el request "checkin" para obtener las capabilities del agente.
     * Cuando termina el checkin, envía la confirmación al Agente Mapa.
     * @author Emilien
     */
    private void recibirDatos(){
        ACLMessage outbox = crearMensaje(getAid(),controlador_id,ACLMessage.QUERY_REF,
                "", conversation_id, replyWith);
        send(outbox);

        try {
            ACLMessage inbox = receiveACLMessage();
            System.out.println("Recibe mi percepciones: " + inbox.getContent());
            if(inbox.getPerformativeInt() == ACLMessage.INFORM){
                bateria = jsonobj.decodeBattery(inbox.getContent());
                serverPos = jsonobj.decodeGPS(inbox.getContent());
                // ArrayList<Integer> radar = jsonobj.decodeRadar(inbox.getContent());
                estoy_en_objetivo = jsonobj.decodeGoal(inbox.getContent());
                
                ACLMessage outbox2 = crearMensaje(getAid(),agente_mapa_id,ACLMessage.REQUEST,
                    jsonobj.encodeUpdateMap(inbox.getContent()), "", "");
                send(outbox2);
                
            } else
                System.out.println("Fallo en recibirDatos(). Details: " + jsonobj.decodeError(inbox.getPerformative()));
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException en recibirDatos(). Error: "+ex.getMessage());
        }  
    }


    /**
     * @brief Comportamiento del agente
     * @author Javier Bejar Mendez, Dani
     */
    @Override
    public void execute(){
        
        // Recibimos el conversation ID del Agente Mapa
        getConversationID();
        
        // Registramos el vehículo, obtenemos sus capabilities y enviamos la confirmación
        // al agente mapa de que nos hemos registrado.
        checkin();
        recibirDatos();
        
        // Luego notificamos al agente mapa acerca de nuestros datos. Dani: ¿Tal vez no es necesario
        // que el agente Mapa sepa las capabilities de los vehículos?
        //notifyParam();
        
        // Bucle principal
        /*do{
            
            
        } while(true);*/
        
        //Operaciones y notificaciones para terminar la ejecución del agente correctamente
    }
    
    /**
     * @author Javier Bejar Mendez
     */
    public AgentType getTipo(){
        return tipo;
    }
    /**
     * @author Javier Bejar Mendez
     */
    public int getBateria(){
        return bateria;
    }
    /**
     * @author Javier Bejar Mendez
     */
    public int getFuelrate(){
        return this.fuelrate;
    }
    /**
     * @author Javier Bejar Mendez
     */
    public int getRange(){
        return this.range;
    }
    /**
     * @author Javier Bejar Mendez
     */
    public boolean getFly(){
        return this.fly;
    }
    /**
     * @author Emilien
     */
    public Posicion getServerPos(){
        return this.serverPos;
    }

    /**
     * @author Emilien
     */
    public boolean getEstoyEnObjetivo(){
        return this.estoy_en_objetivo;
    }
}
