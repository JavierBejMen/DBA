    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gugelcar;

import es.upv.dsic.gti_ia.core.AgentID;
/**
 *
 * @author Daniel
 */
public class main {
    
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        GugelCar.connect();
        try {
            GugelCar c = new GugelCar(new AgentID("sadfasdfsadf"), "map9");
            c.run();
        } catch (Exception ex) {
            System.out.println("Error: "+ex.getMessage());
        }
    }
}
