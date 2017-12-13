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
public class AgenteVehiculo extends SingleAgent{
    
    private final AgentID aid; //ID de este agente
    private final AgentID agente_mapa_id; //ID del agente mapa
    private final AgentID controlador_id; //ID del controador del servidor
    //Puede ser que necesitemos los ID de los demás agentes vehiculos, el martes lo resolvemos
    
    
    private String conversation_id; //ID de la conversación de la sesión
    
    //Atributos propios de cada agente
    private AgentType tipo;
    private int bateria;
    private int fuelrate;
    private int range;
    private boolean fly;
    private boolean estoy_en_objetivo;
    private Posicion serverPos; //No tengo claro si tenemos distinción entre internalPos y serverPos, el martes lo resolvemos.
    
    //Comunicacion
    private final JSON jsonobj;
    private ACLMessage outbox = null;
    private ACLMessage inbox = null;
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
     * @brief Constructor
     * @author Javier Bejar Mendez
     */
    public AgenteVehiculo(AgentID aid, String cid, AgentID mapaid, AgentID controlid) throws Exception{
        super(aid);
        this.conversation_id = cid;
        this.aid = aid;
        this.agente_mapa_id = mapaid;
        this.controlador_id = controlid;
        jsonobj = new JSON();
    }
    
    /**
     * @brief Realiza el request "checkin" para obtener la información del agente
     * @author Javier Bejar Mendez
     */
    private void registrarse(){

        outbox = new ACLMessage();
        outbox.setSender(aid);
        outbox.setReceiver(controlador_id);
        outbox.setContent(jsonobj.encodeCheckin());
        outbox.setConversationId(conversation_id);
        outbox.setPerformative(ACLMessage.REQUEST);
    
        this.send(outbox);
        try{
            inbox = receiveACLMessage();
        }catch(Exception ex){
            System.out.println("Error al recivir respuesta del chek-in del controlador"+ex.getMessage());
        }
    
        if(inbox.getPerformativeInt() == ACLMessage.INFORM){
            ArrayList<Object> capabilities = jsonobj.decodeCapabilities(inbox.getContent());
            this.fuelrate = (int)capabilities.get(0);
            this.range = (int)capabilities.get(1);
            this.fly = (boolean)capabilities.get(2);
            setType();
            
            System.out.print("Agente "+this.aid.toString()+" Se ha suscrito con éxito con los siguientes parámetros:"
            + "\nfuelrate: "+this.fuelrate+"\nrange: "+this.range+"\nfly: ");
            if(fly)
                System.out.println("true");
            else
                System.out.println("flase");
            System.out.println("Se le ha asignado el tipo: "+this.tipo);
            
        }else{
            System.out.println("Error: In check-in conversation, received: "+jsonobj.decodeErrorControlador(inbox.getPerformative()));
        }
    }
    /**
     * Notifica al agente mapa los parámetros recibidos por el controlador
     * @author Javier Bejar Mendez
     */
    private void notifyParam(){
        outbox = new ACLMessage();
        outbox.setSender(aid);
        outbox.setReceiver(this.agente_mapa_id);
        outbox.setConversationId("notifyParam");
        outbox.setContent(jsonobj.encondeAgentParam(this));
        outbox.setPerformative(ACLMessage.INFORM);
        
        this.send(outbox);
    }
    
    /**
     * @brief Comportamiento del agente
     * @author Javier Bejar Mendez
     */
    @Override
    public void execute(){
        //Primero se registra y controlamos que la operación ha salido correctamente
        registrarse();
        
        //Luego notificamos al agente mapa el tipo de vehiculo y demas datos
        notifyParam();
        
        //Bucle principal
        do{
            
            
        }while(true);
        
        //Operaciones y notificaciones para terminar la ejecución del agente correctamente
    }
    
    /**
     * @author Javier Bejar Mendez
     */
    public AgentType getTipo(){
        return this.tipo;
    }
    /**
     * @author Javier Bejar Mendez
     */
    public int getBateria(){
        return this.bateria;
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
}
