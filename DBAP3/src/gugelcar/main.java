    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gugelcar;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
/**
 *
 * @author Daniel
 */
public class main {

     /**
     * @param args the command line arguments
     * @author Emilien Giard
     */
    public static void main(String[] args) {

        String controlador_name = "Cerastes";
        String agente_mapa_name = "AgenteMapa";
        String vehiculo1_name = "vehiculo1";
        String vehiculo2_name = "vehiculo2";
        String vehiculo3_name = "vehiculo3";
        String vehiculo4_name = "vehiculo4";
        
        String mapName = "map1";
        
        AgentID controlador_id, agente_mapa_id, vehiculo1_id, vehiculo2_id, vehiculo3_id, vehiculo4_id;
        
        try {
            //Conectarse al servidor
            AgentsConnection.connect("isg2.ugr.es", 6000, controlador_name, "Boyero", "Parra", false);
            
            //Creacion de los IDs
            controlador_id = new AgentID(controlador_name);
            agente_mapa_id = new AgentID(agente_mapa_name);
            vehiculo1_id = new AgentID(vehiculo1_name);
            vehiculo2_id = new AgentID(vehiculo2_name);
            vehiculo3_id = new AgentID(vehiculo3_name);
            vehiculo4_id = new AgentID(vehiculo4_name);
            
            //Creacion de los Agentes
            AgenteMapa agente_mapa = new AgenteMapa(agente_mapa_id, mapName, controlador_id,
                    vehiculo1_id, vehiculo2_id, vehiculo3_id, vehiculo4_id);
            
            AgenteVehiculo vehiculo1 = new AgenteVehiculo(vehiculo1_id, agente_mapa_id, controlador_id);
            AgenteVehiculo vehiculo2 = new AgenteVehiculo(vehiculo2_id, agente_mapa_id, controlador_id);
            AgenteVehiculo vehiculo3 = new AgenteVehiculo(vehiculo3_id, agente_mapa_id, controlador_id);
            AgenteVehiculo vehiculo4 = new AgenteVehiculo(vehiculo4_id, agente_mapa_id, controlador_id);
            
            //Ejecución de los Agentes
            agente_mapa.start();
            vehiculo1.start();
            vehiculo2.start();
            vehiculo3.start();
            vehiculo4.start();
            
            System.out.println("llego");
        } catch (Exception ex) {
            System.out.println("Error en main(): "+ex.getMessage());
        }
    }
}
