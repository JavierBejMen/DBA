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
     * El metodo hace tal
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
    mapa = "map1";
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
        
        //mover(decidir());
        this.enviarMensajeControlador(json.encodeMove(Movimientos.moveSW, this.clave_acceso));
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

public static void connect(){
    AgentsConnection.connect("isg2.ugr.es",PORT,VIRTUAL_HOST,USER,PASSWORD,false);
}
 /**
     * @param world
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>jorge: prototipo</li>
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
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>jorge : prototipo</li>
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
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>jorge: prototipo</li>
     * 			<li>Daniel Díaz Pareja:programación interna </li>
     *         </ul>
     */
public void logout(){
    String mensaje = json.encodeLogout(clave_acceso);
    enviarMensajeControlador(mensaje);
}
 /**
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>jorge : prototipo</li>
     * 			<li>@donas11 :programación interna </li>
     *         </ul>
     */
public void mover(String direccion){
    Movimientos envio = null;
    if ("RF".equals(direccion))
        refuel();
    else {
        switch(direccion){  
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
        this.enviarMensajeControlador(json.encodeMove(envio,this.clave_acceso));
    }
}
 /**
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>jorge: prototipo</li>
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
public String decidir(){
    String decision = null;
    //Movimientos accion;
    float min_dist = 999999;
  
  if(bateria ==1){
      //accion = Movimientos.refuel;
      decision="RF";
  }else{
      /*int i= pos_x-1;
      int j= pos_y-1;
      while(i<pos_x+1){
          while(j<pos_y+1){
              
               if(((map.get(i)).get(j)!=-1 ) && (i != pos_x || j != pos_y )  &&(min_dist >lectura_escaner.get(posMatriz(i-pos_x,j-pos_y)))){
                   min_dist = lectura_escaner.get(posMatriz(i-pos_x,j-pos_y));
                   //accion=i-pos_x+j-pos_y;
               }
               j++;
          }
          i++;
      }*/
      if(decision!="RF"){
        float menor=9999;
        int filaMenor=0;
        int colMenor=0;
        int filaMen=0;
        int colMen=0;
        for (int i = 1; i <= 3; i++) {  //
              for (int j = 1; j <= 3; j++) {
                  if (lectura_escaner.get(posMatriz(i,j)) <= menor) {
                      menor = lectura_escaner.get(posMatriz(i, j));
                      filaMen=filaMenor;
                      colMen=colMenor;
                      filaMenor = i;
                      colMenor = j;
                  } 
              }           
        }

        if((lectura_radar.get(posMatriz(filaMenor,colMenor))) == 1){
            filaMenor = filaMen;
            colMenor = colMen;
        }
        switch(filaMenor){
            case (1):
                  switch(colMenor){
                      case (1): decision="NE";
                      break;
                      case (2): decision="N";
                      break;
                      case (3): decision="NW";
                      break;
                  }
            break;
            case (2):
                  switch(colMenor){
                      case (1): decision="E";
                      break;
                      case (2): decision="OBJ";//estamos en el objetivo
                      break;
                      case (3): decision="W";
                      break;
                  }
            break;
            case (3):
                  switch(colMenor){
                      case (1): decision="SE";
                      break;
                      case (2): decision="S";
                      break;
                      case (3): decision="SW";
                      break;
                  }
            break;
        }
      }  
      
      
      
   }
  return decision;
    
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
