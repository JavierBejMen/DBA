/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gugelcar;

import JSON.JSON;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.ArrayList;

/**
 *
 * @author Javier Bejar Mendez
 */
public class AgenteMapa extends SingleAgent{
    
    private AgentID aid; //ID de este agente
    private AgentID controlador_id; //ID del agente controlador del servidor
    
    private AgentID aid1; //ID del Agente Vehiculo 1
    private AgentID aid2; //ID del Agente Vehiculo 2
    private AgentID aid3; //ID del Agente Vehiculo 3
    private AgentID aid4; //ID del Agente Vehiculo 4
    
    private String conversation_id; //ID de la conversación de la sesión actual
    
    //Atributos propios del Agente Mapa
    private Mapa map;
    private String nameMap;
    private final JSON json;
    
    /**
     * @brief Constructor
     * @author Javier Bejar Mendez y Emilien Giard
     */
    public AgenteMapa(AgentID aid, String nameMap, AgentID controlador_id) throws Exception{
        super(aid);
        this.aid = aid;
        this.nameMap = nameMap;
        this.controlador_id = controlador_id;
        json = new JSON();
    }
    
   
    /**
     * Metodo que se suscribe y recibe el id de conversación
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
            System.out.println(inbox.getContent());
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
     * Metodo que crea los vehiculos
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

        //Controlamos que todos los agentes han sido creados correctamente y recibimos sus datos

        //Inicializamos el mapa
        
        //bucle principal
        do{
            
        }while(true);
        
        //Guardamos los datos necesarios para las siguientes ejecuciones(mapa interno)
        
        //Terminamos la sesión y realizamos las comunicaciones en caso de ser necesarias con el resto de agentes
    }
    
}
