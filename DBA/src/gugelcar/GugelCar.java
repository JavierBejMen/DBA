    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gugelcar;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dani
 */

public class GugelCar extends SingleAgent {
private ArrayList<ArrayList<Integer>> map;
private static final String HOST = "isg2.ugr.es";
private static final String USER = "Boyero";
private static final String PASSWORD = "Parra";
private String clave_acceso;
private static final int PORT = 6000;
private static final String VIRTUAL_HOST = "Cerastes";
private int bateria;
private int pos_x;
private int pos_y;
private ArrayList<Float> lectura_escaner;
private ArrayList<Integer> lectura_radar;
private Estados estado_actual;
private final JSON json;
private int pasos;

 /**
     * Función auxiliar para saber posición en el Arraylist 
     * @param fil número de fila en la matriz
     * @param col número de fila en la matriz
     * @autor <ul>
     * 			<li>@donas11 :prototipo</li>
     * 			<li>@donas11 :programación interna </li>
     *         </ul>
     */
private int posMatriz(int fil,int col){
          return ((map.get(0).size())*fil)+col;
    }
 /**
     * Constructor
     * @param aid
     * @throws java.lang.Exception
     * @autor <ul>
     * 			<li>Jorge Echevarria Tello: prototipo</li>
     * 			<li>Daniel :programación interna </li>
     *         </ul>
     */
public GugelCar(AgentID aid) throws Exception{
    super(aid);
    map = new ArrayList();
    lectura_escaner = new ArrayList();
    json = new JSON();
    pasos = 0;
}

 /**
     * 
     * @param 
     * @throws java.lang.Exception
     * @autor <ul>
     * 			<li>Emilien: programación inicial </li>
     * 			<li>Daniel :programación interna </li>
     *         </ul>
     */
@Override
public void execute(){
    String radar, scanner, gps, battery, traza, mapa;
    mapa = "map5";
    login(mapa);
    
    do {
        radar = recibirMensajeControlador();
        scanner = recibirMensajeControlador();
        gps = recibirMensajeControlador();
        battery = recibirMensajeControlador();
    
        this.lectura_escaner = json.decodeScanner(scanner);
        this.lectura_radar = json.decodeRadar(radar);
        this.pos_x = json.decodeGPS(gps).x;
        this.pos_y = json.decodeGPS(gps).y;
        this.bateria = json.decodeBattery(battery);

        decidir();

        this.estado_actual = json.decodeEstado(recibirMensajeControlador());
        pasos++;
        
    } while (!estoyEnObjetivo());

    logout();
    // Cuando se hace el logout se quedan estos mensajes encolados, los recibo
    // todos para poder conseguir el último mensaje: la traza
    radar = recibirMensajeControlador();
    scanner = recibirMensajeControlador();
    gps = recibirMensajeControlador();
    battery = recibirMensajeControlador();
    this.estado_actual = json.decodeEstado(recibirMensajeControlador());
    radar = recibirMensajeControlador();
    scanner = recibirMensajeControlador();
    gps = recibirMensajeControlador();
    battery = recibirMensajeControlador();
    traza = recibirMensajeControlador();
    json.guardarTraza(traza, mapa+".png");
}

/**
     * @brief Crea conexión con el servidor
     * @autor <ul>
     * 			<li>Jorge: prototipo</li>
     * 			<li>Daniel Díaz Pareja :programación interna </li>
     *         </ul>
     */
public static void connect(){
    AgentsConnection.connect("isg2.ugr.es",PORT,VIRTUAL_HOST,USER,PASSWORD,false);
}
 /**
     * @param world
     * @brief Sirve para hacer login en un mapa
     * @autor <ul>
     * 			<li>Jorge: prototipo</li>
     * 			<li>Daniel Díaz Pareja :programación interna </li>
     *         </ul>
     */
public void login(String world){
    String nombre = this.getAid().getLocalName();
    String mensaje = json.encodeLoginControlador(world, nombre, nombre, nombre, nombre);
    enviarMensajeControlador(mensaje);
    String respuesta = recibirMensajeControlador();
    if (respuesta.contains("trace"))
        respuesta = recibirMensajeControlador();
    
    clave_acceso = json.decodeClave(respuesta);
}

/**
 * @author Daniel Díaz Pareja
 * @brief Envía un mensaje al controlador y devuelve la respuesta
 * @param mensaje Mensaje a enviar al controlador
 */
public void enviarMensajeControlador(String mensaje){
    ACLMessage outbox = new ACLMessage();
    outbox.setSender(this.getAid());
    outbox.setReceiver(new AgentID(VIRTUAL_HOST));
    outbox.setContent(mensaje);
    this.send(outbox);
}

 /**
     * @brief Recibe mensajes del controlador
     * @autor <ul>
     * 			<li>Jorge : prototipo</li>
     * 			<li>Daniel :programación interna </li>
     *         </ul>
     * @return Mensaje del controlador
     */
public String recibirMensajeControlador(){
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
     * @brief Método para hacer logout
     * @autor <ul>
     * 			<li>Jorge: prototipo</li>
     * 			<li>Daniel Díaz Pareja:programación interna </li>
     *         </ul>
     */
public void logout(){
    String mensaje = json.encodeLogout(clave_acceso);
    enviarMensajeControlador(mensaje);
}


 /**
     * @brief Metodo que en función de la dirección a la que deba moverse se escoge su enum correspondiente
     * @autor <ul>
     * 			<li>Jorge : prototipo</li>
     * 			<li>@donas11 :programación interna </li>
     *         </ul>
     */
/*public void mover(Movimientos move){
    //Movimientos envio = null;
    if (Movimientos.refuel.equals(move)){
        refuel();
          System.out.println("\n\nEstoy en mover refuel");

    }else {
      /*  switch(direccion){  
          case ("NE"):    
              envio=Movimientos.moveNE;
          break;
          case ("N"):
              envio=Movimientos.moveN;
          break;
          case ("NW"):
              envio=Movimientos.moveNW;
          break;
          case ("E"):
              envio=Movimientos.moveE;
          break;
          case ("W"):
              envio=Movimientos.moveW;
          break;
          case ("SE"):
              envio=Movimientos.moveSE;
          break;
          case ("S"):
              envio=Movimientos.moveS;
          break;
          case ("SW"):
             envio=Movimientos.moveSW;
          break;
      }
          System.out.println("\n\nEstoy en mover en movimientos");

        this.enviarMensajeControlador(json.encodeMove(move,this.clave_acceso));
    }
}
*/

 /**
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>Jorge: prototipo</li>
     * 			<li>:programación interna </li>
     *         </ul>
     */
public void refuel(){
    this.enviarMensajeControlador(json.encodeRefuel(clave_acceso));
}
 /**
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>jorge : prototipo</li>
     * 			<li> :programación interna </li>
     *         </ul>
     */
public void actualizarMapa(){

}
 /**
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>jorge: prototipo</li>
     * 			<li>@donas11 :programación interna </li>
     * 			<li>Javier :idea inicial</li>
     *         </ul>
     */
public void decidir(){
    Movimientos mover = null;
    
  if(bateria == 1){
      mover = Movimientos.refuel;
  }else{
      float menor = 9999;
      int movimiento=0;
      for (int i = 6; i < 9; i++) {
              if(!(lectura_radar.get(i).equals(1))){
                if (lectura_escaner.get(i) <= menor) {
                    menor = lectura_escaner.get(i);
                    movimiento = i;
              }
            }           
      }
      for (int i = 11; i < 14; i++) {
              if(!(lectura_radar.get(i).equals(1))){
                if (lectura_escaner.get(i) <= menor && i!=12) {
        
                    menor = lectura_escaner.get(i);
                    movimiento = i;

              }
            }           
      }
      for (int i = 16; i < 19; i++) {
              if(!(lectura_radar.get(i).equals(1))){
                if (lectura_escaner.get(i) <= menor) {
                    menor = lectura_escaner.get(i);
                    movimiento = i;

              }
            }           
      }
      
      switch(movimiento){
        case (8): mover = Movimientos.moveNE;
        break;
        case (7): mover = Movimientos.moveN;
        break;
        case (6): mover = Movimientos.moveNW;
        break;
        case (13): mover=Movimientos.moveE;
        break;
        case (12): System.out.println("\n\nMe quedo quieto");
        break;
        case (11): mover=Movimientos.moveW;
        break;
        case (18): mover= Movimientos.moveSE;
        break;
        case (17): mover=Movimientos.moveS;
        break;
        case (16): mover=Movimientos.moveSW;
        break;
      
   }
  }
  this.enviarMensajeControlador(json.encodeMove(mover,this.clave_acceso));

}
 /**
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>jorge: prototipo</li>
     * 			<li>@donas11 :programación interna </li>
     *         </ul>
     */
public boolean estoyEnObjetivo(){
    boolean obj=false;
    if(lectura_radar.get(12) == 2){
        obj=true;
        System.out.println("Estoy en objetivo. Pasos dados: "+pasos);
    }

    return obj;
}

}
