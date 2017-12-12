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
     */
    public static void main(String[] args) {
        
        AgentsConnection.connect("isg2.ugr.es",6000, "Cerastes", "Boyero", "Parra", false); 
        
        
        try {
            
        } catch (Exception ex) {
            System.out.println("Error: "+ex.getMessage());
        }
    }
}
