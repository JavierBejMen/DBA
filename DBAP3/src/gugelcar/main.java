    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gugelcar;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import java.util.ArrayList;
/**
 *
 * @author Daniel
 */
public class main {

     /**
     * @param args the command line arguments
     * @author Emilien Giard, Javier Bejar Mendez
     */
    public static void main(String[] args) {
        System.out.println("Creando los agentes y recursos necesarios para lanzarlos");
        String controlador_name = "Cerastes";
        String agente_mapa_name = "ag_mapa";
        String vehiculo1_name = "ve_1";
        String vehiculo2_name = "ve_2";
        String vehiculo3_name = "ve_3";
        String vehiculo4_name = "ve_4";
        
        String mapName = "map1";
        
        AgentID controlador_id, agente_mapa_id;
        ArrayList<AgentID> vehiculos = new ArrayList();
        
        try {
            //Conectarse al servidor
            AgentsConnection.connect("isg2.ugr.es", 6000, controlador_name, "Boyero", "Parra", false);
            
            //Creacion de los IDs
            controlador_id = new AgentID(controlador_name);
            agente_mapa_id = new AgentID(agente_mapa_name);
            vehiculos.add(new AgentID(vehiculo1_name));
            vehiculos.add(new AgentID(vehiculo2_name));
            vehiculos.add(new AgentID(vehiculo3_name));
            vehiculos.add(new AgentID(vehiculo4_name));
            
            //Da error ya que hay que añadir la batería global.
            //Creacion de los Agentes
            AgenteMapa agente_mapa = new AgenteMapa(agente_mapa_id, mapName, controlador_id,
                    vehiculos);
            
            AgenteVehiculo vehiculo1 = new AgenteVehiculo(vehiculos.get(0), agente_mapa_id, controlador_id);
            AgenteVehiculo vehiculo2 = new AgenteVehiculo(vehiculos.get(1), agente_mapa_id, controlador_id);
            AgenteVehiculo vehiculo3 = new AgenteVehiculo(vehiculos.get(2), agente_mapa_id, controlador_id);
            AgenteVehiculo vehiculo4 = new AgenteVehiculo(vehiculos.get(3), agente_mapa_id, controlador_id);
            
            //Ejecución de los Agentes
            agente_mapa.start();
            vehiculo1.start();
            vehiculo2.start();
            vehiculo3.start();
            vehiculo4.start();
            
            System.out.println("Init<========= OK");
            
        } catch (Exception ex) {
            System.out.println("Excepción en main(): "+ex.getMessage());
        }
    }
}
