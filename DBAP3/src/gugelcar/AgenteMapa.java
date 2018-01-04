/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gugelcar;

import JSON.JSON;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import gugelcar.exceptions.ExceptionBadParam;
import gugelcar.exceptions.ExceptionNonInitialized;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class AgenteMapa extends SingleAgent{
    
    private final AgentID controlador_id; //ID del agente controlador del servidor
    private ArrayList<AgentID> vehiculos; // IDs de los vehiculos
    
    private String conversation_id; //ID de la conversación de la sesión actual
    
    //Atributos propios del Agente Mapa
    private Mapa mapa;
    private final String nameMap;
    private boolean objetivo_encontrado;
    private int iteracion;
    private int energy; // Batería global
    private final int tam_mapa = 111;
    private boolean turno_ocupado;
    private int numeroCancel;
    //Comunicacion
    private final JSON jsonobj;
    
    /**
     * @param aid del AgenteMapa
     * @param nameMap el nombre de la mapa
     * @param controlador_id el id del servidor
     * @param aids vector de agentes, deben ser 4
     * Constructor
     * @author Javier Bejar Mendez, Emilien Giard, Dani, Jorge
     * @throws java.lang.Exception
     */
    public AgenteMapa(AgentID aid, String nameMap, AgentID controlador_id, 
            ArrayList<AgentID> aids) throws Exception{
        super(aid);
        this.vehiculos = aids;
        this.nameMap = nameMap;
        this.controlador_id = controlador_id;
        jsonobj = new JSON();
        this.actualizaDatosMapaImportado();
        this.turno_ocupado = false;
        this.numeroCancel = 0;
    }
    
   
    /**
     * Metodo que se suscribe y recibe y guarda el id de conversación
     * @author Emilien Giard, Dani
     */
    private void subscribe(){
        String world = jsonobj.encodeWorld(nameMap);
        
        ACLMessage outbox = crearMensaje(getAid(), controlador_id, 
                ACLMessage.SUBSCRIBE, world, "", "");
        send(outbox);
        
        try {
            ACLMessage inbox = receiveACLMessage();
            if (inbox.getPerformative().equals("INFORM")) {
                conversation_id = inbox.getConversationId();
                System.out.println("\nRecibido conversation id "
                +inbox.getConversationId()+" de "+inbox.getSender().getLocalName());
            } else {
                System.out.println("No se ha podido suscribir. Error: "+
                   jsonobj.decodeError(inbox.getContent()));
            }
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException en subscribe(). Error: " + ex.getMessage());
        }
    }
    /**
     * Envía el conversation ID, el mapa y si se ha encontrado el objetivo a los 4 vehículos
     * @author Dani
     */
    private void enviarEstadoInicial() {
        for (AgentID vehiculo : vehiculos)
            this.enviarMapa(vehiculo);
    }
    
    /**
     * Metodo que actualiza el mapa en funcion de las percepciones de un vehiculo
     * @param vision visión actual del vehículo
     * @param pos posicion en el mapa global del vehículo
     * @param obj_enc si se ha encontrado el objetivo
     * @author Emilien Giard, Jorge, Dani
     */
    private void updateMap(JSONArray vision, Posicion pos, boolean obj_enc) {
        objetivo_encontrado = obj_enc;
        int dist;
        if (vision.length() == 9)
            dist = 1;
        else if (vision.length() == 25)
            dist = 2;
        else
            dist = 5;
        try {
            Posicion nueva = new Posicion(pos.getX()-dist,pos.getY()-dist);
            int range = (int)sqrt(vision.length());
            for (int fil = 0; fil < range; fil++){
                for (int col = 0; col < range; col++){
                    mapa.set(nueva, vision.getInt(fil*range+col));
                    nueva.setX(nueva.getX()+1);
                }
                nueva.setX(nueva.getX()-range);
                nueva.setY(nueva.getY()+1);
            }
        } catch (ExceptionBadParam | ExceptionNonInitialized ex) {
            System.out.println("Excepción en updateMap(). Mensaje: "+ex.getMessage());
        }
    }

    /**
     * Metodo que envia el mapa global a un vehiculo (fase de barrido).
     * @param vehiculo AgentID del vehículo a enviar el mapa
     * @author Emilien, Jorge, Dani
     */
    private void enviarMapa(AgentID vehiculo) {
        ACLMessage outbox = crearMensaje(getAid(), vehiculo, ACLMessage.INFORM,
                jsonobj.encodeMapa(mapa, objetivo_encontrado),
                conversation_id, "");
        send(outbox);
    }
    
    /**
     * Metodo que envia si el vehiculo debe cierra la sesion o no.
     * @param vehiculo AgentID del vehículo a enviar el mapa
     * @author Emilien
     */
    private void enviarInformCancel(AgentID vehiculo) {
        ACLMessage outbox = crearMensaje(getAid(), vehiculo, ACLMessage.INFORM,
                jsonobj.encodeCierraSesion(numeroCancel == 4),
                conversation_id, "");
        send(outbox);
    }
    
    /**
     * Envía un mensaje para desloguearse del servidor. 
     * Recibe y guarda la traza de la sesión.
     * @author Javier Bejar Mendez, Dani
     */
    private void logout(){
        
        ACLMessage outbox = this.crearMensaje(getAid(), controlador_id,
                ACLMessage.CANCEL, "", "", "");
        send(outbox);
        try {
            ACLMessage respuesta_agree = receiveACLMessage();
            ACLMessage respuesta_inform = receiveACLMessage();
            System.out.println("Logout completado. Traza guardada en: "+
                    nameMap +".png");
            jsonobj.guardarTraza(respuesta_inform.getContent(), nameMap + ".png");

        } catch (InterruptedException ex) {
            System.out.println("InterruptedException en logOut()."
                + " Error: " + ex.getMessage());
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

    /**
     * Metodo que crea los vehiculos y recibe los messajes de los vehiculos
     * @author Emilien Giard, Javier Bejar Mendez, Dani, Jorge
     */
     @Override
    public void execute(){
        //logout(); // Remove when agents will close the session
        subscribe();
        enviarEstadoInicial();

        // bucle principal: espera los mesajes de los otros agentes
        do {
            try {
                ACLMessage inbox = receiveACLMessage();
                String command = jsonobj.decodeCommandVehiculo(inbox.getContent());
                System.out.println("\nRecibido command "
                    + command +" de "+inbox.getSender().getLocalName());
                switch (command) {
                    case "checked-in":
                        System.out.println("Recibida confirmación de que el vehiculo "
                                + inbox.getSender().getLocalName() +
                                " ha hecho el checkin.\n");
                        break;
                    case "update-map":
                        JSONArray vision = jsonobj.decodeVision(inbox.getContent());
                        Posicion pos_vehiculo = jsonobj.decodePos(inbox.getContent());
                        boolean obj_enc = jsonobj.decodeObjetivoEncontrado(inbox.getContent());
                        updateMap(vision, pos_vehiculo, obj_enc);
                        enviarMapa(inbox.getSender());
                        System.out.println("Mapa global actualizado. Se envia a "+inbox.getSender().getLocalName());
                        break;
                    case "export-map":
                        exportarMapa();
                        break;
                    case "cancel":
                        numeroCancel ++;
                        enviarInformCancel(inbox.getSender());
                        break;
                    default:
                        ACLMessage outbox = crearMensaje(getAid(), inbox.getSender(), 
                            ACLMessage.NOT_UNDERSTOOD, "", "", "");
                        this.send(outbox);
                        System.out.println("Agente "+getName()+
                                " envia NOT UNDERSTOOD a vehiculo "
                                +inbox.getSender().getLocalName());
                        break;
                }
            } catch (InterruptedException ex) {
                System.out.println("Excepción al recibir mensaje en execute(). Mensaje: "+ex.getMessage());
            }
        } while((!objetivo_encontrado) || (numeroCancel <= 4));
        
        
        System.out.println("Objetivo encontrado! o recibe 4 cancel de los vehiculos");
        //Guardamos los datos necesarios para las siguientes ejecuciones(mapa interno)
        exportarMapa();
        //Terminamos la sesión y realizamos las comunicaciones en caso de ser necesarias con el resto de agentes
        logout();
    }
    
     /**
     * Exporta el mapa en un archivo llamado "mapa.json"
     * @author Jorge
     */
    private void exportarMapa(){
        jsonobj.exportMapa(mapa, objetivo_encontrado, iteracion+1, nameMap);
    }
    
    /**
     * Importa el mapa desde un archivo llamado "mapa.json"
     * @author Jorge
     */
    private JSONObject importarMapa(){
        JSONObject obj = jsonobj.importMapa(nameMap);
        return obj;
    }
    
    /**
     * 
     * @author Jorge, Dani
     */
    private void actualizaDatosMapaImportado(){
        try {
            JSONObject obj = importarMapa();
            iteracion = obj.getInt("iteracion");
            if (iteracion == 0)
                mapa = new Mapa(tam_mapa);
            else
            {
                mapa = new Mapa(1);
                int tam = obj.getInt("tamanio");
                mapa.setTam(tam);
                Integer[][] m = new Integer[tam][tam];
                JSONArray array = obj.getJSONArray("mapa");

                for (int fil = 0; fil < tam; fil++)
                    for (int col = 0; col < tam; col++)
                        m[fil][col] = (Integer)array.getInt(fil*tam+col);
                
                mapa.setMapa(m);
            }
            objetivo_encontrado = obj.getBoolean("encontrado");
        } catch (ExceptionBadParam ex) {
            System.out.println("Excepcion en actualizaDatosMapaImportado: "+ex.getMessage());
        }
    }
    /**
     * set de turno_ocupado
     * @author Jorge
     */
    private void setTurnoOcupado(boolean t){
        this.turno_ocupado = t;
    }
    /**
     * get de turno_ocupado
     * @author Jorge
     */
    private boolean getTurnoOcupado(){
        return this.turno_ocupado;
    }
    /**
     * Metodo que envia el turno a un vehiculo (fase de barrido).
     * @param vehiculo AgentID del vehículo a enviar el mapa
     * @author Jorge
     */
    private void enviarTurno(AgentID vehiculo) {
        ACLMessage outbox = crearMensaje(getAid(), vehiculo, ACLMessage.INFORM,
                jsonobj.encodeTurno(turno_ocupado),
                conversation_id, "");
        if(!getTurnoOcupado()) setTurnoOcupado(true);
        send(outbox);
    }
    /**
     * Metodo que envia el turno a un vehiculo (fase de barrido).
     * @param vehiculo AgentID del vehículo a enviar el mapac
     * @author Jorge
     */
    private void recibirFinTurno() throws InterruptedException {
        ACLMessage inbox = receiveACLMessage();
        boolean turno = jsonobj.decodeTurno(inbox.getContent());
        this.setTurnoOcupado(turno);
    }
}
