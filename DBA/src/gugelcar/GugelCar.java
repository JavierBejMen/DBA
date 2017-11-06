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

/**
 *
 * @author dani
 */

public class GugelCar extends SingleAgent {
//private ArrayList<ArrayList<Integer>> map;
private static final String HOST = "isg2.ugr.es";
private static final String USER = "Boyero";
private static final String PASSWORD = "Parra";
private String clave_acceso;
private static final int PORT = 6000;
private static final String VIRTUAL_HOST = "Cerastes";
private int bateria;
private int pos_x; //posicion en server
private int pos_y; //posicion en server
private ArrayList<Float> lectura_escaner;
private ArrayList<Integer> lectura_radar;
private Estados estado_actual;
private final JSON json;
private int pasos;

/*
    Gestion del mapa interno (map)
    si en server estamos en la posicion (20,20)
    en map estaremos en la posicion (20+MARGIN_X, 20+MARGIN_Y)
    se han definido funciones privadas para el manejo de las coordenadas

*/
private static final int TAM_X = 2000; //mapa x
private static final int TAM_Y = 2000; //mapa y
private static final int MARGIN_X = 100; //margen del map x
private static final int MARGIN_Y = 100; //margen del map y
private int[][] map;
private int mpos_x; //posicion en map
private int mpos_y; //posicion en map

/**
 * @author Javier Bejar Mendez
 * @brief transforma la posicion xy del vector a la posicion en map, se puede 
 * usar para obtener la poscion xy del radar o scaner, ejemplo: 
 *  int size = 5 //el radar es un vector de tamaño 25, es una matriz 5x5 luego tam = 5
 *  int[] auxpos;
 *  auxpos = this.vector_to_map_pos(xy, size);
 *  auxpos[0] equivale a la posicion x en el mapa de la posicion xy del radar
 * @param xy posicion en el vector
 * @param tam tamaño de la columan o fila, se supone que trabajamos en matriz cuadrada
 * @return int[] pos de 2 posiciones, pos[0] contiene la posicion x en map y pos[1] la posicion y en map
 */
private int[] vector_to_map_pos(int xy, int tam){
    
    int[] pos = new int[2];
    pos[0] = this.mpos_x - 2 + xy/tam;
    pos[1] = this.mpos_y - 2 + xy%tam;
    
    return pos;
}

/**
 * @author Javier Bejar Mendez
 * @brief transforma la posicion xy del vector a la posicion en server
 * @param xy posicion en el vector
 * @param tam tamaño de la columan o fila, se supone que trabajamos en matriz cuadrada
 * @return int[] pos de 2 posiciones, pos[0] contiene la posicion x en server y pos[1] la posicion y en server
 */
private int[] vector_to_server_pos(int xy, int tam){
    int[] pos = new int[2];
    pos[0] = this.pos_x - 2 + xy/tam;
    pos[1] = this.pos_y - 2 + xy%tam;
    
    return pos;
}

/**
 * @author Javier Bejar Mendez
 * @brief transforma la posicion (x,y) del server a la posicion en map
 * @param x posicion x en server
 * @param y posicion y en server
 * @return int[] pos de 2 posiciones, pos[0] contiene la posicion x en map y pos[1] la posicion y en map
 */
private int[] server_to_map_pos(int x, int y){
    int[] pos = new int[2];
    pos[0] = x - MARGIN_X;
    pos[1] = y - MARGIN_Y;
    
    return pos;
}

/**
 * @author Javier Bejar Mendez
 * @brief transforma la posicion (x,y) del map a la posicion en server
 * @param x posicion x en map
 * @param y posicion y en map
 * @return int[] pos de 2 posiciones, pos[0] contiene la posicion x en server y pos[1] la posicion y en server
 */
private int[] map_to_server_pos(int x, int y){
    int[] pos = new int[2];
    pos[0] = x + MARGIN_X;
    pos[1] = y + MARGIN_Y;
    
    return pos;
}

/**
 * @author Javier Bejar Mendez
 * @brief actualiza la posicion actual del map en funcion de la posicion actual del server
 */
private void actualiza_mpos(){
    this.mpos_x = this.pos_x + MARGIN_X;
    this.mpos_y = this.pos_y + MARGIN_Y;
}


 /**
     * Función auxiliar para saber posición en el Arraylist 
     * @param fil número de fila en la matriz
     * @param col número de fila en la matriz
     * @autor <ul>
     * 			<li>@donas11 :prototipo</li>
     * 			<li>@donas11 :programación interna </li>
     *         </ul>
     *//*
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
    lectura_escaner = new ArrayList();
    json = new JSON();
    pasos = 0;
    map = new int[TAM_X][TAM_Y]; //Inicializo el mapa a 0, indicando las veces que se ha pasado por la posicion i j
    for (int i = 0; i < TAM_X; i++)
       for (int j = 0; j < TAM_Y; j++)
           map[i][j]=0;
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
    mapa = "map10";
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
        
        map[pos_x][pos_y] = map[pos_x][pos_y]+1; //Incremento en 1 indicando que se ha pasado una vez más por esa posición
        decidir_v2();

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
     * @brief Actualiza el mapa con las lecturas del radar e incrementa las posiciones no visitadas
     * @autor <ul>
     * 			<li>jorge : prototipo</li>
     * 			<li> :programación interna </li>
     *                  <li>Javier bejar : implementacion</li>
     *         </ul>
     */
public void actualizarMapa(){ //Recorremos toda la matriz incrementando cada posición del mapa que no sea obstaculo
    //Actualización de obstaculos y objetivo
    int size = this.lectura_radar.size();
    int[] auxpos;
    for(int i = 0; i < size; ++i){ //recorremos todo el vector donde esta el radar
        auxpos = this.vector_to_map_pos(i, 5);
       if(map[auxpos[0]][auxpos[1]] != -1){ //Si nuestro mapa no es obstaculo (!=-1)
           if(this.lectura_radar.get(i) == 1){ //Si el radar  es obstaculo (=1)
                map[auxpos[0]][auxpos[1]] = -1; //añadimos el obstaculo a nuestro mapa
           }
           else if(this.lectura_radar.get(i) == 2){//Si el radar es el objetivo
               map[auxpos[0]][auxpos[1]] = -2; //añadimos el objetivo a nuestro mapa
           }
       }
    }
    
    //Incrementamos todas las casillas que no sean ni obstaculo ni objetivo
    for(int i = 0; i < TAM_X; ++i){
        for(int j = 0; j < TAM_Y; ++j){
            if(map[i][j] >= 0)
                ++map[i][j];
        }
    }
}

/**
     * @brief selecciona como movimiento la casilla de alrededor que mas tiempo lleve sin visitar
     * @autor <ul>
     *
     *                  <li>Javier bejar: esqueleto</li>
     *         </ul>
     * @return movimiento, el movimiento seleccionado
     */
public Movimientos menos_reciente(){
    Movimientos movimiento = null;
    int mas_viejo = 0;
    for(int i = 0; i < 2; ++i){
        for(int j = 0; i < 2;++j){
            
        }
    }
    
    return movimiento;
}
/**
     * @brief decide en función de decidir_v2() y llama al metodo menos_reciente en caso de bucle
     * @autor <ul>
     *
     *                  <li>Javier bejar: esqueleto</li>
     *         </ul>
     */
public void decidir_v3(){
    
    
}
 /**
     * @brief El metodo decide que movimiento realiza, simplemente teniendo en 
     *  cuenta la menor distancia siempre y cuando no sea una pared
     * @autor <ul>
     * 			<li>jorge: prototipo</li>
     * 			<li>@donas11 y Jorge :programación interna </li>
     * 			<li>Javier y Jorge :idea inicial</li>
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
     * @brief El metodo decide que movimiento realiza, teniendo en 
     *  cuenta la menor distancia siempre y cuando no sea una pared, y si ha pasado o no antes
     * @autor <ul>
     * 			<li>jorge: prototipo</li>
     * 			<li>Jorge :programación interna </li>
     * 			<li>Javier y Jorge :idea inicial</li>
     *         </ul>
     */
public void decidir_v2(){
    Movimientos mover = null;
  if(bateria == 1){
      mover = Movimientos.refuel;
  }else{
      float menor = 9999;
      int movimiento=0;
      for (int i = 6; i < 9; i++) {
              if(!(lectura_radar.get(i).equals(1))){ //Si no hay una pared..
                if ((lectura_escaner.get(i) <= menor) && !he_pasado(i)) { //Si la posición tiene un valor menor de scanner y no he pasado
                    menor = lectura_escaner.get(i); //Guardo la menor distancia hasta el momento
                    movimiento = i; //Guardo el mejor movimiento hasta el momento
              }
            }           
      }
      for (int i = 11; i < 14; i++) {
              if(!(lectura_radar.get(i).equals(1)) && (i!=12)){//Si no hay una pared y no me quedo quieto
                if((lectura_escaner.get(i) <= menor) && !he_pasado(i)){//Si la posición tiene un valor menor de scanner y no he pasado
                    menor = lectura_escaner.get(i);//Guardo la menor distancia hasta el momento
                    movimiento = i;//Guardo el mejor movimiento hasta el momento
              }
            }
      }
      for (int i = 16; i < 19; i++) {
              if(!(lectura_radar.get(i).equals(1))){//Si no hay una pared..
                if ((lectura_escaner.get(i) <= menor) && !he_pasado(i)) {//Si la posición tiene un valor menor de scanner y no he pasado
                    menor = lectura_escaner.get(i);//Guardo la menor distancia hasta el momento
                    movimiento = i;//Guardo el mejor movimiento hasta el momento
              }
            }           
      }
        
    switch(movimiento){ //Transformo el int al movimiento equivalente
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
    System.out.println("\n\nMe muevo a "+mover);

  this.enviarMensajeControlador(json.encodeMove(mover,this.clave_acceso));

}

 /**
     * @return True si ha pasado, false en caso contrario
     * @brief El metodo comprueba si ya ha pasado por ese camino
     * @param movimiento, Se trata del movimiento que queremos ver si podemos realizar
     * @autor <ul>
     * 			<li>jorge: prototipo</li>
     * 			<li>@jorge :programación interna </li>
     *         </ul>
     */
public boolean he_pasado(int movimiento){
    boolean pasado = true;
    
    switch(movimiento){ //Dependiendo del movimiento que corresponda
            case (8): if((pos_x+1 > TAM_X )|| (pos_y-1 < 0)){ //Compruebo si existe esa posición del mapa
                        pasado = true;
                        }else if(map[pos_x+1][pos_y-1] == 0) pasado = false; //Si no he pasado..
            break;
            case (7):  if(pos_y-1 < 0 ){
                        pasado = true;
                        }else if(this.map[pos_x][pos_y-1] == 0) pasado = false;
            break;
            case (6):  if(pos_x-1 < 0 || pos_y-1 < 0){
                        pasado = true;
                        }else if(map[pos_x-1][pos_y-1] == 0) pasado = false;
            break;
            case (13):  if(pos_x+1 > TAM_X){
                        pasado = true;
                        }else if(map[pos_x+1][pos_y] == 0) pasado = false;
            break;
            case (11): if(pos_x-1 < 0){
                        pasado = true;
                        }else if(map[pos_x-1][pos_y] == 0)pasado = false;
            break;
            case (18):  if((pos_x+1 > TAM_X) || (pos_y+1 > TAM_Y)){
                        pasado = true;
                        }else if(map[pos_x+1][pos_y+1] == 0) pasado = false;
            break;
            case (17):  if(pos_y+1 > TAM_Y){
                        pasado = true;
                        }else if(map[pos_x][pos_y+1] == 0) pasado = false;
            break;
            case (16):  
                if((pos_x-1 < 0) || (pos_y+1 > TAM_Y)){
                        pasado = true;
                        }else if(map[pos_x-1][pos_y+1] == 0) pasado = false;
            break;
            case (12):  pasado = true;
            break;
       }
    return pasado; //Devuelvo un boolean indicando si he pasado o no (true/false)
}
 /**
     * @brief El metodo comprueba si estoy en el objetivo
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
