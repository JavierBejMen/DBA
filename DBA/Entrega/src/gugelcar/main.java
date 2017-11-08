/* Grupo C
*  Práctica 2 de DBA
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
            GugelCar c = new GugelCar(new AgentID("bmw"), "map11");
            c.run();
        } catch (Exception ex) {
            System.out.println("Error al crear el agente.");
        }
    }
}
