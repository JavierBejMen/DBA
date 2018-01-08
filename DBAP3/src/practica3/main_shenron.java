package practica3;


import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 *
 * @author Dani
 */
public class main_shenron {
    
    public static void main(String[] args) {
        
        try {
            AgentsConnection.connect("isg2.ugr.es", 6000, "test", "Boyero", "Parra", false);
            Shenron agente = new Shenron(new AgentID("ShenronGrupo3"));
            agente.start();
        } catch (Exception ex) {
            System.out.println("Excepción en main. Mensaje: "+ ex.getMessage());
        }
    }
}