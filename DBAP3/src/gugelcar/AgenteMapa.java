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
    
    /**
     * @brief Constructor
     * @author Javier Bejar Mendez
     */
    public AgenteMapa(AgentID aid) throws Exception{
        super(aid);
        this.aid = aid;
    }
    
   
    /**
     * Metodo que se suscribe y recibe el id de conversación
     */
    public void suscribe(){}
    
    
     @Override
    public void execute(){
        //Nos suscribimos y controlamos errores
        suscribe();
        
        //Difundimos el id de conversación y creamos los agentes
        
        //Controlamos que todos los agentes han sido creados correctamente y recibimos sus datos
        
        //Inicializamos el mapa
        
        //bucle principal
        do{
            
        }while(true);
        
        //Guardamos los datos necesarios para las siguientes ejecuciones(mapa interno)
        
        //Terminamos la sesión y realizamos las comunicaciones en caso de ser necesarias con el resto de agentes
    }
    
}
