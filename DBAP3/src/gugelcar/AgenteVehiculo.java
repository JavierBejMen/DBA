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
    
    private AgentID aid; //ID de este agente
    private AgentID agente_mapa_id; //ID del agente mapa
    private AgentID controlador_id; //ID del controador del servidor
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
    JSON jsonobj;
    
    
    /**
     * @brief Constructor
     * @author Javier Bejar Mendez
     */
    public AgenteVehiculo(AgentID aid, String cid) throws Exception{
        super(aid);
        this.conversation_id = cid;
        this.aid = aid;
    }
    
    /**
     * @brief Realiza el request "checkin" para obtener la información del agente
     * @author Javier Bejar Mendez
     */
    public void registrarse(){
    ACLMessage outbox = null;
    
    outbox = new ACLMessage();
    outbox.setSender(aid);
    outbox.setReceiver(controlador_id);
    outbox.setContent(jsonobj.encodeCheckin());
    outbox.setConversationId(conversation_id);
    outbox.setPerformative(ACLMessage.REQUEST);
    
    ACLMessage inbox = null;
    
    this.send(outbox);
    try{
    inbox = receiveACLMessage();
    }catch(Exception ex){
        System.out.println("Error al recivir respuesta del chek-in del controlador"+ex.getMessage());
    }
    
    if(inbox.getPerformative() == jsonobj.decodeErrorControlador(inbox.getPerformative())){
        System.out.println("Error: el controlador no entiende el check-in");
    }else if(inbox.getPerformativeInt() == ACLMessage.INFORM){
        ArrayList<Object> capabilities = jsonobj.decodeCapabilities(inbox.getContent());
        this.fuelrate = (int)capabilities.get(0);
        this.range = (int)capabilities.get(1);
        this.fly = (boolean)capabilities.get(2);
    }
    
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
        
        //Bucle principal
        do{
            
            
        }while(true);
        
        //Operaciones y notificaciones para terminar la ejecución del agente correctamente
    }
}
