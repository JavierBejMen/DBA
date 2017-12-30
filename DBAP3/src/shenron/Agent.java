/*
 * Realizado por: Daniel Díaz Pareja
 */
package shenron;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.Scanner;

/**
 *
 * @author Daniel Díaz Pareja
 */
public class Agent extends SingleAgent {
    private final AgentID shenron = new AgentID("Shenron");
    private final String user = "Boyero";
    private final String pass = "Parra";
    
    public Agent(AgentID aid) throws Exception  {
        super(aid);
    }
    
    @Override
    public void execute(){
        int opcion;
        Scanner keyboard = new Scanner(System.in);
        boolean en_menu = true;
        while (en_menu){
            System.out.println("Introduce el número de la acción para realizarla");
            System.out.println("1: Reiniciar el servidor");
            System.out.println("2: Consulta del último registro de actividad del controlador.");
            System.out.println("3: Salir");
            opcion = keyboard.nextInt();
            
            switch (opcion){
                case 1:
                    reiniciarServidor();
                    break;
                case 2:
                    consultarUltimoRegistro();
                    break;
                case 3:
                    en_menu = false;
                    break;
            }
        }
    }
    
    private void reiniciarServidor(){
        String content = "{\"user\":\""+user+"\",\"password\":\""+pass+"\"}";
        ACLMessage outbox = crearMensaje(this.getAid(), shenron, ACLMessage.REQUEST,
            content, "", "");
        send(outbox);
        
        try {
            ACLMessage inbox = receiveACLMessage();
            if (inbox.getPerformativeInt() == ACLMessage.FAILURE)
                System.out.println("FAILURE recibido de Shenron. Puede ser por error "
                        + "en la identificación o por un problema técnico inesperado "
                        + "del servidor. Mensaje de respuesta: " + inbox.getContent());
            else
                System.out.println("Éxito. Mensaje de respuesta: "+inbox.getContent());
            
        } catch (InterruptedException ex) {
            System.out.println("Excepción en reiniciarServidor(). Mensaje: " + ex.getMessage());
        }
    }
    
    private void consultarUltimoRegistro(){
        String content = "{\"user\":\""+user+"\",\"password\":\""+pass+"\"}";
        ACLMessage outbox = crearMensaje(this.getAid(), shenron, ACLMessage.QUERY_REF,
            content, "", "");
        send(outbox);
        
        try {
            ACLMessage inbox = receiveACLMessage();
            
            if (inbox.getPerformativeInt() == ACLMessage.FAILURE)
                System.out.println("FAILURE recibido de Shenron. Puede ser por error "
                        + "en la identificación o por un problema técnico inesperado "
                        + "del servidor. Mensaje de respuesta: " + inbox.getContent());
            else
                System.out.println("Éxito. Mensaje de respuesta: "+inbox.getContent());
            
        } catch (InterruptedException ex) {
            System.out.println("Excepción en consultarUltimoRegistro(). Mensaje: " + ex.getMessage());
        }
    }
    
    /**
     * Crea un mensaje con los parámetros especificados.
     * @param sender
     * @param receiver
     * @param performative
     * @param content
     * @param conv_id
     * @param in_reply_to
     * @return ACLMessage con los parámetros especificados
     * @author Dani
     */
    private ACLMessage crearMensaje(AgentID sender, AgentID receiver, int performative,
            String content, String conv_id, String in_reply_to){
        
        ACLMessage outbox = new ACLMessage(performative);
        outbox.setSender(sender);
        outbox.setReceiver(receiver);
        outbox.setContent(content);
        outbox.setConversationId(conv_id);
        outbox.setInReplyTo(in_reply_to);
        
        return outbox;
    }
}
