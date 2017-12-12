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

/**
 *
 * @author Javier Bejar Mendez
 */
public class AgenteMapa extends SingleAgent{
    
    private final AgentID aid; //ID de este agente
    private final AgentID controlador_id; //ID del agente controlador del servidor
    
    private AgentID aid1; //ID del Agente Vehiculo 1
    private AgentID aid2; //ID del Agente Vehiculo 2
    private AgentID aid3; //ID del Agente Vehiculo 3
    private AgentID aid4; //ID del Agente Vehiculo 4
    
    private String conversation_id; //ID de la conversación de la sesión actual
    
    //Atributos propios del Agente Mapa
    private Mapa map;
    private final String nameMap;
    private final JSON json;
    
    /**
     * @param aid del AgenteMapa
     * @param nameMap el nombre de la mapa
     * @param controlador_id el id del servidor
     * @param aid1 el id del vehiculo 1
     * @param aid2 el id del vehiculo 2
     * @param aid3 el id del vehiculo 3
     * @param aid4 el id del vehiculo 4
     * @brief Constructor
     * @author Javier Bejar Mendez y Emilien Giard
     */
    public AgenteMapa(AgentID aid, String nameMap, AgentID controlador_id, AgentID aid1, AgentID aid2, AgentID aid3, AgentID aid4) throws Exception{
        super(aid);
        this.aid = aid;
        this.aid1 = aid1;
        this.aid2 = aid2;
        this.aid3 = aid3;
        this.aid4 = aid4;
        this.nameMap = nameMap;
        this.controlador_id = controlador_id;
        json = new JSON();
    }
    
   
    /**
     * @brief Metodo que se suscribe y recibe el id de conversación
     * @author Emilien Giard
     */
    public void suscribe(){
        String world = json.encodeWorld(this.nameMap);
        ACLMessage outbox = new ACLMessage();
        outbox.setSender(this.getAid());
        outbox.setReceiver(this.controlador_id);
        outbox.setContent(world);
        outbox.setPerformative("SUBSCRIBE");
        this.send(outbox);
        
        try {
            ACLMessage inbox = this.receiveACLMessage();
            if (inbox.getPerformative().equals("INFORM")) {
                this.conversation_id = inbox.getConversationId();
                System.out.println("\nRecibido conversation id "
                +inbox.getConversationId()+" de "+inbox.getSender().getLocalName());
            } else {
                System.out.println("\nRecibido error "
                +inbox.getPerformative()+" de razon "+inbox.getContent());
            }
        } catch (InterruptedException ex) {
            System.out.println("Error al recibir mensaje" + ex.getMessage());
        }
    }

    /**
     * @param percepciones de un vehiculo
     * @brief Metodo que actualiza la mapa en funcion de los percepciones de un vehiculo
     * @author Emilien Giard
     */
    public void updateMap(Integer[][] percepciones) {
    }

    /**
     * @brief Metodo que envia la mapa global a un vehiculo
     * @author Emilien Giard
     */
    public void enviarMapa() {
    }

    /**
     * Metodo que crea los vehiculos y recibe los messajes de los vehiculos
     * @author Emilien Giard
     */
     @Override
    public void execute(){
        //Nos suscribimos y controlamos errores
        do {
            suscribe();
        } while(this.conversation_id != null);
        AgenteVehiculo vehiculo1, vehiculo2, vehiculo3, vehiculo4;

        //Difundimos el id de conversación y creamos los agentes
        try {
            this.aid1 = new AgentID("vehiculo1");
            vehiculo1 = new AgenteVehiculo(this.aid1, this.conversation_id);
            vehiculo1.execute();

            vehiculo2 = new AgenteVehiculo(this.aid2, this.conversation_id);
            vehiculo2.execute();

            vehiculo3 = new AgenteVehiculo(this.aid3, this.conversation_id);
            vehiculo3.execute();

            vehiculo4 = new AgenteVehiculo(this.aid4, this.conversation_id);
            vehiculo4.execute();
        } catch (Exception ex) {
            System.out.println("Error al creacion del vehiculo:" + ex.getMessage());
        }

        //Inicializamos el mapa
        
        //bucle principal
        do {
            try {
                ACLMessage inbox = this.receiveACLMessage();
                String command = json.decodeCommandVehiculo(inbox.getContent());
                System.out.println("\nRecibido command"
                    + command +" de "+inbox.getSender().getLocalName());
                if (command.equals("update-map")) {
                    Integer[][] percepciones = json.decodePercepciones(inbox.getContent());
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
