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
    private Mapa map;
    private String nameMap;
    private String conversation_id;
    
    public AgenteMapa(AgentID aid) throws Exception{
        
        super(aid);
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
        
        //bucle principal
        do{
            
        }while(true);
        
        //Guardamos los datos necesarios para las siguientes ejecuciones(mapa interno)
        
        //Terminamos la sesión y realizamos las comunicaciones en caso de ser necesarias con el resto de agentes
    }
    
}
