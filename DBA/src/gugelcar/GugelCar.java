    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gugelcar;

import static gugelcar.Movimientos.REFUEL;
import java.util.ArrayList;

/**
 *
 * @author dani
 */

public class GugelCar {
private ArrayList<ArrayList<Integer>> c;
private String login;
private String password;
private String clave_acceso;
private int numero_mapa;
private int bateria;
private int pos_x;
private int pos_y;
private ArrayList<Float> lectura_escaner;
private Estados estado_actual;

 /**
     * El metodo hace tal
     * @autor <ul>
     * 			<li>Jorge Echevarria Tello: prototipo</li>
     * 			<li> :programación interna </li>
     *         </ul>
     */
public GugelCar(String aid){

}
 /**
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>: prototipo</li>
     * 			<li> :programación interna </li>
     *         </ul>
     */
public void login(){

}
 /**
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>: prototipo</li>
     * 			<li> :programación interna </li>
     *         </ul>
     */
public void logout(){

}
 /**
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>: prototipo</li>
     * 			<li> :programación interna </li>
     *         </ul>
     */
public void mover(String direccion){

}
 /**
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>: prototipo</li>
     * 			<li>@donas11 :programación interna </li>
     *         </ul>
     */
public boolean refuel(){
    return bateria<=2;
}
 /**
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>: prototipo</li>
     * 			<li> :programación interna </li>
     *         </ul>
     */
public void actualizarMapa(){

}
 /**
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>jorge: prototipo</li>
     * 			<li> :programación interna </li>
     *         </ul>
     */
public String decidir(){
    String decision = null;
    Movimientos accion;
    int min_dist = 999999;
  
  if(refuel()){
      accion = REFUEL;
  }else{
      int i= pos_x-1;
      int j= pos_y-1;
      while(i<pos_x+1){
          while(j<pos_y+1){
               if(((map.get(i)).get(j)!=-1 )&& (i != pos_x OR j != pos_y )  &&(min_dist >distancia[i-pos_x][j-pos_y]) ){
                   min_dist = distancia[i-pos_x][j-pos_y];
                   accion=i-pos_x+j-pos_y;
               }
               j++;
          }
          i++;
      }
      
      
   }
  return decision;
    
}
 /**
     * @brief El metodo hace tal
     * @autor <ul>
     * 			<li>jorge: prototipo</li>
     * 			<li> :programación interna </li>
     *         </ul>
     */
public void estoyEnObjetivo(){

}

}
