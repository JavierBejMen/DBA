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
    
    private String conversation_id;
    private int bateria;
    private int fuelrate;
    private int range;
    private boolean fly;
    private boolean estoy_en_objetivo;
    private Posicion serverPos;
    
    
    public AgenteVehiculo(AgentID aid, String cid) throws Exception{
        super(aid);
        this.conversation_id = cid;
    }
    
    /**
     * @brief Realiza el request "checkin" para obtener la información del agente
     * @author Javier Bejar Mendez
     */
    public void registrarse(){}
    
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
