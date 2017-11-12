package gugelcar;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.ArrayList;
import gugelcar.exceptions.*;

/**
 * @brief Clase con la funcionalidad del agente que simula un coche de google
 */

public class GugelCar extends SingleAgent {

private static final String HOST = "isg2.ugr.es";
private static final String USER = "Boyero";
private static final String PASSWORD = "Parra";
private String clave_acceso;
private final String mapa;
private static final int PORT = 6000;
private static final String VIRTUAL_HOST = "Cerastes";
private int bateria;
Posicion serverPos;
private ArrayList<Float> lectura_escaner;
private ArrayList<Integer> lectura_radar;
private Estados estado_actual;
private final JSON json;
private int pasos;
boolean obj = false;
/*
    Gestion del mapa interno (map)
    si en server estamos en la posicion (20,20)
    en map estaremos en la posicion (20+MARGIN_X, 20+MARGIN_Y)
    se han definido funciones privadas para el manejo de las coordenadas

*/
private static final int TAM = 2000; //mapa 
private static final int MARGIN = 100; //margen del map 
Mapa map;
Posicion internalPos;





    /**
     * @brief Constructor
     * @param aid ID del agente
     * @param mapa Mapa al que se conectará el agente
     * @throws java.lang.Exception
     * @autor <ul>
     *              <li>Jorge Echevarria Tello: cabecera</li>
     *              <li>Daniel Díaz Pareja: implementación </li>
     *         </ul>
     */
public GugelCar(AgentID aid, String mapa) throws Exception{
    super(aid);
    this.mapa = mapa;
    lectura_escaner = new ArrayList();
    json = new JSON();
    pasos = 0;
    
    this.map = new Mapa(TAM);
    this.serverPos = new Posicion();
    this.internalPos = new Posicion();
}

    /**
     * @brief Lo que hará el agente al crearse
     * @autor <ul>
     * 			<li>Emilien: implementación inicial </li>
     * 			<li>Daniel Díaz Pareja: implementación final </li>
     *         </ul>
     */
@Override
public void execute(){
    String gps;
    login();
    
    do {// Mientras no estemos en el objetivo, lo buscamos:
        
        // Recibimos los mensajes de los sensores
        //radar = recibirMensajeControlador();
        //scanner = recibirMensajeControlador();
        
        //battery = recibirMensajeControlador();
        
        // Recibimos y decodificamos los mensajes de los sensores
        this.lectura_radar = json.decodeRadar(recibirMensajeControlador());
        this.lectura_escaner = json.decodeScanner(recibirMensajeControlador());
        gps = recibirMensajeControlador();
        this.serverPos.setX(json.decodeGPS(gps).x);
        this.serverPos.setY(json.decodeGPS(gps).y);
        this.bateria = json.decodeBattery(recibirMensajeControlador());
        
        //Version usada en v2
        /*map[pos_x][pos_y] = map[pos_x][pos_y]+1; //Incremento en 1 indicando que se ha pasado una vez más por esa posición
        decidir_v2();
        */
        
        //Version v3
       //decidir_v3();
        
        // Recibimos y decodificamos el estado
        this.estado_actual = json.decodeEstado(recibirMensajeControlador());
        pasos++; // Contador de los pasos que da el agente para resolver el mapa 
             //(por tener algo de feedback además de la traza
        
        
    } while (!estoyEnObjetivo());

    logout();
}

/**
     * @brief Crea la conexión con el servidor. Es static para poder utilizarla
     * sin tener que instanciar la clase.
     * @autor <ul>
     *              <li>Jorge: cabecera</li>
     *              <li>Daniel Díaz Pareja: implementación </li>
     *         </ul>
     */
public static void connect(){
    AgentsConnection.connect("isg2.ugr.es",PORT,VIRTUAL_HOST,USER,PASSWORD,false);
}
 /**
     * @brief Hace el login del agente en el mapa dado en el constructor. Este
     * agente tendrá todos los sensores (hemos decidido hacerlo así).
     * @autor <ul>
     *              <li>Jorge: cabecera</li>
     *              <li>Daniel Díaz Pareja :implementación </li>
     *         </ul>
     */
private void login(){
    String nombre = this.getAid().getLocalName();
    String mensaje = json.encodeLoginControlador(mapa, nombre, nombre, nombre, nombre);
    enviarMensajeControlador(mensaje);
    String respuesta = recibirMensajeControlador();
    if (respuesta.contains("trace"))
        respuesta = recibirMensajeControlador();
    
    clave_acceso = json.decodeClave(respuesta);
}

/**
 * @author Daniel Díaz Pareja
 * @brief Envía un mensaje al controlador
 * @param mensaje Mensaje a enviar al controlador
 */
private void enviarMensajeControlador(String mensaje){
    ACLMessage outbox = new ACLMessage();
    outbox.setSender(this.getAid());
    outbox.setReceiver(new AgentID(VIRTUAL_HOST));
    outbox.setContent(mensaje);
    this.send(outbox);
}

 /**
     * @brief Recibe un mensaje del controlador y lo imprime por pantalla.
     * @autor <ul>
     * 			<li>Jorge : cabecera</li>
     * 			<li>Daniel Díaz Pareja: implementación </li>
     *         </ul>
     * @return Mensaje del controlador
     */
private String recibirMensajeControlador(){
    String mensaje = "vacio";
    try {
        ACLMessage inbox=this.receiveACLMessage();
        mensaje=inbox.getContent();
        System.out.println("\nRecibido mensaje "
            +inbox.getContent()+" de "+inbox.getSender().getLocalName());
    } catch (InterruptedException ex) {
        System.out.println("Error al recibir mensaje");
    }
    return mensaje;
}
 /**
     * @brief Método para hacer logout del servidor.
     * @autor <ul>
     * 			<li>Jorge: cabecera</li>
     * 			<li>Daniel Díaz Pareja: implementación</li>
     *         </ul>
     */
private void logout(){
    String mensaje = json.encodeLogout(clave_acceso);
    enviarMensajeControlador(mensaje);
    
    // Cuando se hace el logout se quedan estos mensajes encolados, los recibimos
    // todos para poder conseguir el último mensaje: la traza
    recibirMensajeControlador(); // radar
    recibirMensajeControlador(); // scanner
    recibirMensajeControlador(); // gps
    recibirMensajeControlador(); // battery
    this.estado_actual = json.decodeEstado(recibirMensajeControlador());
    
    recibirMensajeControlador(); // radar
    recibirMensajeControlador(); // scanner
    recibirMensajeControlador(); // gps
    recibirMensajeControlador(); // battery
    json.guardarTraza(recibirMensajeControlador(), mapa+".png"); // se guarda
        // en la carpeta del proyecto netbeans
}


 /**
     * @brief Envía un mensaje al controlador para recargar la batería del agente.
     * @autor <ul>
     *              <li>Jorge: cabecera</li>
     *              <li>Daniel Díaz Pareja: implementación </li>
     *         </ul>
     */
private void refuel(){
    this.enviarMensajeControlador(json.encodeRefuel(clave_acceso));
}
 
private boolean estoyEnObjetivo(){
   
    if(lectura_radar.get(12) == 2){
        obj=true;
        System.out.println("Estoy en objetivo. Pasos dados: "+pasos);
    }

    return obj;
}

}
