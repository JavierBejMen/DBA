package shenron;


import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 *
 * @author Dani
 */
public class main {
    
    public static void main(String[] args) {
        
        try {
            AgentsConnection.connect("isg2.ugr.es", 6000, "test", "Boyero", "Parra", false);
            Agent agente = new Agent(new AgentID("Grupo3"));
            agente.start();
        } catch (Exception ex) {
            System.out.println("Excepción en main. Mensaje: "+ ex.getMessage());
        }
    }
}