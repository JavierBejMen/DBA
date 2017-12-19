/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gugelcar;

import JSON.JSON;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier Bejar Mendez,Emilien Giard
 */
public class AgenteMapa extends SingleAgent{
    
    private final AgentID controlador_id; //ID del agente controlador del servidor
    private AgentID aid1; //ID del Agente Vehiculo 1
    private AgentID aid2; //ID del Agente Vehiculo 2
    private AgentID aid3; //ID del Agente Vehiculo 3
    private AgentID aid4; //ID del Agente Vehiculo 4
    
    private String conversation_id; //ID de la conversación de la sesión actual
    
    //Atributos propios del Agente Mapa
    private Mapa map;
    private final String nameMap;
    private boolean objetivo_encontrado;
    
    //Comunicacion
    private final JSON jsonobj;
    
    /**
     * @param aid del AgenteMapa
     * @param nameMap el nombre de la mapa
     * @param controlador_id el id del servidor
     * @param aid1 el id del vehiculo 1
     * @param aid2 el id del vehiculo 2
     * @param aid3 el id del vehiculo 3
     * @param aid4 el id del vehiculo 4
     * @brief Constructor
     * @author Javier Bejar Mendez, Emilien Giard, Dani
     */
    public AgenteMapa(AgentID aid, String nameMap, AgentID controlador_id, AgentID aid1, AgentID aid2, AgentID aid3, AgentID aid4) throws Exception{
        super(aid);
        this.aid1 = aid1;
        this.aid2 = aid2;
        this.aid3 = aid3;
        this.aid4 = aid4;
        this.nameMap = nameMap;
        this.controlador_id = controlador_id;
        jsonobj = new JSON();
        
        objetivo_encontrado = false; // TODO: Cargar de disco si hemos encontrado el 
            // objetivo en ejecuciones anteriores
    }
    
   
    /**
     * @brief Metodo que se suscribe y recibe y guarda el id de conversación
     * @author Emilien Giard, Dani
     */
    private void subscribe(){
        String world = jsonobj.encodeWorld(nameMap);
        
        ACLMessage outbox = crearMensaje(getAid(), controlador_id, 
                ACLMessage.SUBSCRIBE, world, "", "");
        send(outbox);
        
        try {
            ACLMessage inbox = receiveACLMessage();
            if (inbox.getPerformative().equals("INFORM")) {
                conversation_id = inbox.getConversationId();
                System.out.println("\nRecibido conversation id "
                +inbox.getConversationId()+" de "+inbox.getSender().getLocalName());
            } else {
                System.out.println("No se ha podido suscribir. Error: "+
                   jsonobj.decodeError(inbox.getContent()));
            }
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException en subscribe(). Error: " + ex.getMessage());
        }
    }
    /**
     * Envía el conversation ID a los 4 vehículos
     * @author Dani
     */
    private void enviarConversationID() {
        ACLMessage outbox = crearMensaje(getAid(), aid1, ACLMessage.INFORM,
                jsonobj.encodeObjetivoEncontrado(objetivo_encontrado),
                conversation_id, "");
        send(outbox);

        outbox.setReceiver(aid2);
        send(outbox);

        outbox.setReceiver(aid3);
        send(outbox);

        outbox.setReceiver(aid4);
        send(outbox);
    }
    
    /**
     * @param percepciones de un vehiculo
     * @brief Metodo que actualiza la mapa en funcion de los percepciones de un vehiculo
     * @author Emilien Giard
     */
    private void updateMap(Integer[][] percepciones) {
    }

    /**
     * @brief Metodo que envia la mapa global a un vehiculo
     * @author Emilien Giard
     */
    private void enviarMapa() {
    }
    
    /**
     * Envía un mensaje para desloguearse del servidor. 
     * Recibe y guarda la traza de la sesión.
     * @author Javier Bejar Mendez, Dani
     */
    private void logout(){
        
        ACLMessage outbox = this.crearMensaje(getAid(), controlador_id,
                ACLMessage.CANCEL, "", "", "");
        send(outbox);
        try {
            ACLMessage respuesta_agree = receiveACLMessage();
            ACLMessage respuesta_inform = receiveACLMessage();
            System.out.println("Logout completado. Traza guardada en: "+
                    nameMap +".png");
            jsonobj.guardarTraza(respuesta_inform.getContent(), nameMap + ".png");

        } catch (InterruptedException ex) {
            System.out.println("InterruptedException en logOut()."
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
     * Metodo que crea los vehiculos y recibe los messajes de los vehiculos
     * @author Emilien Giard, Javier Bejar Mendez, Dani
     */
     @Override
    public void execute(){
        int agenteCheckedIn = 0;
        subscribe();
        enviarConversationID();

        //Inicializamos el mapa
        
        // bucle principal: espera los mesajes de los otros agentes
        do {
            System.out.println("agenteCheckedIn " 
                        + agenteCheckedIn);
            // Temporary
            if (agenteCheckedIn == 4) {
                logout();
            }
            try {
                ACLMessage inbox = this.receiveACLMessage();
                String command = jsonobj.decodeCommandVehiculo(inbox.getContent());
                System.out.println("\nRecibido command "
                    + command +" de "+inbox.getSender().getLocalName());

                if (command.equals("checked-in")) {
                    System.out.println("Recibida confirmación de que el vehiculo " 
                        + inbox.getSender().getLocalName() + 
                        " ha hecho el checkin.\n");
                    agenteCheckedIn ++;

                } else if (command.equals("update-map")) {
                    Integer[][] percepciones = jsonobj.decodePercepciones(inbox.getContent());

                    // TODO: update the AgenteMapa's mapa with the perceptions
                    this.updateMap(percepciones);
                    // TODO: send the global map to the other agent and if he is in the objective
                    this.enviarMapa();

                } else if (command.equals("export-map")) {
                } else {
                    ACLMessage outbox = new ACLMessage();
                    outbox.setSender(this.getAid());
                    outbox.setReceiver(new AgentID(inbox.getSender().getLocalName()));
                    outbox.setPerformative("NOT-UNDERSTOOD");
                    outbox.setInReplyTo(inbox.getReplyWith());
                    this.send(outbox);
                }
            } catch (InterruptedException ex) {
                System.out.println("Error al recibir mensaje" + ex.getMessage());
            }
        } while(true);
        
        //Guardamos los datos necesarios para las siguientes ejecuciones(mapa interno)
        
        //Terminamos la sesión y realizamos las comunicaciones en caso de ser necesarias con el resto de agentes
    }
}
