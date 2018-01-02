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
import gugelcar.exceptions.ExceptionBadParam;
import gugelcar.exceptions.ExceptionNonInitialized;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONArray;

public class AgenteVehiculo extends SingleAgent{
    
    private AgentID agente_mapa_id; //ID del agente mapa
    private AgentID controlador_id; //ID del controador del servidor
    //Puede ser que necesitemos los ID de los demás agentes vehiculos, el martes lo resolvemos
    
    private String conversation_id; //ID de la conversación de la sesión
    private String replyWith;
    
    //Atributos propios de cada agente
    private AgentType tipo;
    private int bateria;
    private int energy;
    private int fuelrate;
    private int range;
    private boolean fly;
    private boolean objetivo_encontrado;
    private boolean estoy_en_objetivo;
    private Posicion pos;
    private Mapa mapa;
    
    //Comunicacion
    private JSON jsonobj;
    
    // Movimiento
    private final int borde = 100000;
    private final int objetivo = -100000;
    private final int vehiculo = 100000;
    private final int obstaculo = 10000;

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
        pos = new Posicion();
        mapa = new Mapa(1);
    }
    
    /**
     * Recibe del Agente Mapa el conversation ID, si el objetivo se ha encontrado
     * y el mapa global y los guarda en el estado interno.
     * @todo recibir el mapa global y guardarlo
     * @author Dani
     * 
     */
    private void getEstadoYConversationID() {
        try {
            ACLMessage inbox = receiveACLMessage();
            System.out.println("Vehículo "+getName()+" recibe estado del agente Mapa");
            objetivo_encontrado = jsonobj.decodeObjetivoEncontrado(inbox.getContent());
            mapa = jsonobj.decodeMapa(inbox.getContent());
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
     * Envía el request al agente Mapa con las percepciones locales del agente y
     * recibe la respuesta del agente Mapa, actualizando el mapa y si se ha
     * encontrado el objetivo.
     * 
     * @param percepciones String json con las percepciones del agente.
     * @todo actualizar el mapa de este agente con la respuesta recibida
     * del agente Mapa.
     * @author Dani
     */
    private void sendUpdateMap()
    {
        try {
            int dist;
            if (range == 3)
                dist = 1;
            else if (range == 5)
                dist = 2;
            else
                dist = 5;
 
            Posicion pos_enviar = new Posicion(pos.getX()-dist,pos.getY()-dist);
            ArrayList<Integer> vision = new ArrayList();
            boolean obj_enc = objetivo_encontrado;
            // Actualizar el mapa con lo que se acaba de percibir
            for (int fil = 0; fil < range; fil++){
                for (int col = 0; col < range; col++){
                    if (mapa.get(pos_enviar) == objetivo)
                        obj_enc = true;
                    vision.add(mapa.get(pos_enviar));
                    pos_enviar.setX(pos_enviar.getX()+1);
                }
                pos_enviar.setX(pos_enviar.getX()-range);
                pos_enviar.setY(pos_enviar.getY()+1);
            }
            ACLMessage outbox = crearMensaje(getAid(),agente_mapa_id,ACLMessage.REQUEST,
                    jsonobj.encodeUpdateMap(vision, pos, obj_enc), "", "");
            //System.out.println("Agente " + getName() + " envía updateMap con contenido: "
                    //+ outbox.getContent());
            send(outbox);

            ACLMessage inbox = receiveACLMessage();
            objetivo_encontrado = jsonobj.decodeObjetivoEncontrado(inbox.getContent());
            Mapa map = jsonobj.decodeMapa(inbox.getContent());
            mapa = map;
        } catch (InterruptedException | ExceptionNonInitialized | ExceptionBadParam ex) {
            System.out.println("Excepción en updateMap(). Error: "+ex.getMessage());
        }
    }
    
    /**
     * Mira en las 8 casillas que tiene alrededor para moverse a la que tiene
     * menor valor. Da preferencia al Sur y al Este. 
     * @todo hacer barrido en condiciones, si puede ser que se vayan haciendo
     * diagonales, y que si un vehiculo empieza en el norte de preferencia a ir al sur,
     * si empieza en el este de preferencia ir al oeste, si empieza en el oeste
     * de preferencia al ir al este y si empieza en el sur de preferencia a ir al norte.
     * 
     * @author Dani
     */
    private Movimiento decidirExploracion(){
        Movimiento m = null;
        try {
            int valor;
            int menor = 100000;
            Posicion destino, nueva;
            nueva = new Posicion(pos.getX()-1,pos.getY()-1);
            destino = new Posicion();

            for (int fil = 0; fil < 3; fil++){
                for (int col = 0; col < 3; col++){
                    if (fil != 1 || col != 1){ // para no tener en cuenta la casilla donde está el vehículo.
                        valor = mapa.get(nueva);
                        if (valor <= menor){
                            destino.setX(nueva.getX());
                            destino.setY(nueva.getY());
                            menor = valor;
                        }
                    }
                    nueva.setX(nueva.getX()+1);
                }
                nueva.setX(nueva.getX()-3);
                nueva.setY(nueva.getY()+1);
            }
            m = pos.getMove(destino);
            actualizarPosicion(destino);
        } catch (ExceptionBadParam | ExceptionNonInitialized ex) {
            System.out.println("Excepción en decidirExploracion(). Mensaje: " + ex.getMessage());
        }
        
        return m;
    }
    
    /** Actualiza la posicion actual del agente a la nueva y modifica
     * el valor de la casilla en el mapa sumando 1 para saber que ha pasado
     * por ahí.
     * @author Dani
     */
    private void actualizarPosicion(Posicion nueva){
        try {
            mapa.set(nueva, mapa.get(nueva)+5);
            pos.setX(nueva.getX());
            pos.setY(nueva.getY());
        } catch (ExceptionBadParam | ExceptionNonInitialized ex) {
            System.out.println("Excepción en actualizarPosicion(). Mensaje: " + ex.getMessage());
        }
    }
    
    /**
     * Actualiza el mapa local en función de las percepciones recibidas.
     * Por ejemplo, si una casilla es el límite del mundo, actualiza
     * el valor de dicha casilla a un valor muy alto para que el agente
     * sepa que no tiene que ir allí.
     * @author Dani
     * @param percepciones String con las percepciones del vehículo en formato
     * json.
     */
    private void actualizarMapaLocal(String percepciones){
        try {
            int dist;
            if (range == 3)
                dist = 1;
            else if (range == 5)
                dist = 2;
            else
                dist = 5;
            
            // Posicion siguiente a actualizar
            Posicion pos_act = new Posicion(pos.getX()-dist,pos.getY()-dist);
            JSONArray radar = jsonobj.decodeRadar(percepciones);
            
            // Actualizar el mapa con lo que se acaba de percibir
            for (int fil = 0; fil < range; fil++){
                for (int col = 0; col < range; col++){
                    int valor_radar = radar.getInt(fil*range+col);
                    switch (valor_radar){
                        //case 0: mapa.set(pos_act, mapa.get(pos_act)+1);
                        case 1: mapa.set(pos_act, obstaculo); break;
                        case 2: mapa.set(pos_act, borde); break;
                        case 3: mapa.set(pos_act, objetivo); break;
                        case 4: mapa.set(pos_act, vehiculo); break;
                    }
                    pos_act.setX(pos_act.getX()+1);
                }
                pos_act.setX(pos_act.getX()-range);
                pos_act.setY(pos_act.getY()+1);
            }
        } catch (ExceptionNonInitialized | ExceptionBadParam ex) {
            System.out.println("Excepción en actualizarMapaConPercepciones(). Mensaje: " + ex.getMessage());
        }
    }
    
    /**
     * Realiza el query_ref para obtener las percepciones del agente y actualiza
     * las variables de clase (bateria, energy...). Devuelve las percepciones en una
     * cadena JSON de la forma {"result":{"battery":"...", ....}}
     * @return Cadena JSON de la forma {"result":{"battery":"...", ....}} con las percepciones.
     * @author Emilien, Dani
     */
    private String recibirPercepciones(){
        ACLMessage outbox = crearMensaje(getAid(), controlador_id, ACLMessage.QUERY_REF,
                "", conversation_id, replyWith);
        send(outbox);
        ACLMessage inbox = null;
        try {
            inbox = receiveACLMessage();
            System.out.println("Vehículo " +getName()+ " envía query_ref al servidor "
                    + "y recibe percepciones: " + inbox.getContent());
            if(inbox.getPerformativeInt() == ACLMessage.INFORM){
                bateria = jsonobj.decodeBattery(inbox.getContent());
                pos = jsonobj.decodeGPS(inbox.getContent());
                pos.setX(pos.getX()+6); // Para que, al actualizar el mapa, no se salga
                pos.setY(pos.getY()+6); // del rango de la matriz, desplazamos la posición
                    // 6 casillas hacia abajo y hacia la derecha
                estoy_en_objetivo = jsonobj.decodeGoal(inbox.getContent());
                energy = jsonobj.decodeEnergy(inbox.getContent());
                replyWith = inbox.getReplyWith();
            } else
                System.out.println("Fallo en recibirPercepciones(). Details: " + jsonobj.decodeError(inbox.getPerformative()));
        } catch (InterruptedException | ExceptionNonInitialized ex) {
            System.out.println("Excepción en recibirDatos(). Error: "+ex.getMessage());
        }
        
        return inbox.getContent();
    }

    /**
     * Realiza un refuel con el servidor.
     * @author Dani
     */
    private void refuel(){
        ACLMessage outbox = crearMensaje(getAid(), controlador_id, ACLMessage.REQUEST,
                jsonobj.encodeRefuel(), conversation_id, replyWith);
        send(outbox);

        try {
            ACLMessage inbox = receiveACLMessage();
            System.out.println("Vehículo " +getName()+ " hace refuel y recibe: "
                    +inbox.getContent());
            replyWith = inbox.getReplyWith();
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException en refuel(). Error: "+ex.getMessage());
        }
    }
    /**
     * 
     */
    private void move(Movimiento m){
        ACLMessage outbox = crearMensaje(getAid(), controlador_id, ACLMessage.REQUEST,
                jsonobj.encodeMove(m), conversation_id, replyWith);
        send(outbox);
        
        try {
            ACLMessage inbox = receiveACLMessage();
            System.out.println("Vehículo " +getName()+ " hace el movimiento " +m+
                    " y recibe: "+inbox.getContent());
            replyWith = inbox.getReplyWith();
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException en refuel(). Error: "+ex.getMessage());
        }
    }
    /**
     * Comportamiento del agente
     * @todo Decidir a donde moverse (barrido)
     * @author Javier Bejar Mendez, Dani
     */
    @Override
    public void execute(){
        
        // Recibimos el mapa, si se ha encontrado el objetivo y 
        // el conversation ID del Agente Mapa
        getEstadoYConversationID();
        
        // Registramos el vehículo, obtenemos sus capabilities y enviamos la confirmación
        // al agente mapa de que nos hemos registrado.
        checkin();
        
        String percepciones = recibirPercepciones(); // recibir percepciones del servidor
                                                         // y procesar la respuesta
        
        while ((!objetivo_encontrado || !estoy_en_objetivo) && quedaEnergia() && puedoMoverme())
        {
            actualizarMapaLocal(percepciones);
            sendUpdateMap();
            
            if (bateria <= fuelrate && quedaEnergia())
               refuel();
            
            move(decidirExploracion());
            percepciones = recibirPercepciones();
        }
    }

    /**
     * Devuelve true si tiene batería para moverse, false en caso contrario
     * @author Jorge, Dani
     */
    private boolean puedoMoverme(){
        return (bateria >= fuelrate);
    }
    
    /**
     * Devuelve true si queda energía global, false en caso contrario
     * @author Dani
     */
    private boolean quedaEnergia(){
        return (energy > 0);
    }
    /**
     * Devuelve true si queda energía global, false en caso contrario
     * @author Jorge
     */
    private boolean getEstoyEnObjetivo(){
        return estoy_en_objetivo;
    }
}
