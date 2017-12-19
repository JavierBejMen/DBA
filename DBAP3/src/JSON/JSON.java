/* Crear librería con el siguiente JAR para obtener la funcionalidad de la
biblioteca org.json: 

http://mvnrepository.com/artifact/org.json/json/20160810
*/

package JSON;
import gugelcar.Movimientos;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import gugelcar.AgenteVehiculo;
import gugelcar.Posicion;

/**
 * Clase encargada de obtener los atributos necesarios de un String
 * codificado en formato JSON o de codificar strings para enviar mensajes entre
 * agentes.
 */
public class JSON {
    
    public JSON(){}
    
    /**
     * Crea el string JSON con el mundo especificado. Lo utilizamos en
     * el primer SUBSCRIBE.
     * @param world Mundo de la cadena JSON
     * @return string JSON con el mundo especificado
     * @author Dani
     */
    public String encodeWorld(String world)
    {
        JSONObject obj = new JSONObject();
        obj.put("world", world);
        return obj.toString();
    }
    
    /**
     * Obtiene la razón por la cual no se ha podido realizar 
     * alguna acción en un JSON con el campo "details"
     * @param json cadena JSON que se devuelve al producirse un 
     * error
     * @return Razón por la cual no se ha podido realizar la acción.
     * @author Dani
     */
    public String decodeError(String json){
        JSONObject obj = new JSONObject(json);
        return obj.getString("details");
    }
    
    /**
     * Crea el string JSON para el checkin {"command":"checkin"}.
     * @return string JSON para el checkin {"command":"checkin"}
     * @author Dani
     */
    public String encodeCheckin(){
        JSONObject obj = new JSONObject();
        obj.put("command", "checkin");
        return obj.toString();
    }
    
    /**
     * Crea el string JSON para enviar si se ha
     * encontrado el objetivo a los vehículos.
     * @param obj_enc Booleano que acompaña al campo "objetivo_encontrado"
     * @return String con el campo booleano "objetivo_encontrado"
     * @author Dani
     */
    public String encodeObjetivoEncontrado(boolean obj_enc){
        JSONObject obj = new JSONObject();
        obj.put("result", "OK");
        obj.put("objetivo_encontrado", obj_enc);
        return obj.toString();
    }
    
    /**
     * Decodifica un string JSON con el campo "objetivo_encontrado".
     * @param json Cadena JSON con el campo "objetivo_encontrado"
     * @return valor del campo "objetivo_encontrado"
     * @author Dani
     */
    public boolean decodeObjetivoEncontrado(String json){
        JSONObject obj = new JSONObject(json);
        return obj.getBoolean("objetivo_encontrado");
    }
    
    /**
     * Decodifica un string JSON con las capabilities del agente. Devuelve
     * un ArrayList de objetos en este orden: 1º-fuelrate (int), 2º-range (int),
     * 3º-fly (boolean)
     * @param json cadena JSON con las capabilities
     * @return ArrayList de objetos en el orden especificado.
     * @author Dani
     */
    public ArrayList<Object> decodeCapabilities(String json){
        JSONObject obj = new JSONObject(json);
        JSONObject capabilities = obj.getJSONObject("capabilities");
        
        ArrayList<Object> lista = new ArrayList();
        lista.add(capabilities.getInt("fuelrate"));
        lista.add(capabilities.getInt("range"));
        lista.add(capabilities.getBoolean("fly"));
        
        return lista;
    }
    
    /**
     * Decodifica todos los parámetros del agente en un ArrayList con la siguiente 
     * estructura: {AgentType tipo, int bateria, int fuelrate, int range, boolean fly}.
     * 
     * @author Javier Bejar Mendez
     * @param paramstring
     * @return 
     */
    public ArrayList<Object> decodeAgentParam(String paramstring){
        JSONObject obj = new JSONObject(paramstring);
         
        ArrayList<Object> lista = new ArrayList();
        lista.add(obj.getJSONObject("tipo"));
        lista.add(obj.getInt("bateria"));
        lista.add(obj.getInt("range"));
        lista.add(obj.getInt("fuelrate"));
        lista.add(obj.getBoolean("fly"));
        
        return lista;
    }
    /**
     * Codifica en JSON los parámetros de un vehiculo
     * @param vehiculo
     * @return String en formato JSON con los parámetros del vehículo
     * @author Javier Bejar Mendez
     */
    public String encodeAgentParam(AgenteVehiculo vehiculo){
        JSONObject obj = new JSONObject();
        obj.put("tipo", vehiculo.getTipo());
        obj.put("bateria", vehiculo.getBateria());
        obj.put("range", vehiculo.getRange());
        obj.put("fuelrate", vehiculo.getFuelrate());
        obj.put("fly", vehiculo.getFly());
        
        return obj.toString();
    }
    
    /**
     * Codifica en JSON los parámetros de un vehiculo para la command update-map
     * @param data el misma string que el agente recibe del servidor
     * @return String en formato JSON con las percepciones del vehiculo
     * @author Emilien Giard
     */
    public String encodeUpdateMap(String data){
        JSONObject obj = new JSONObject(data);
        obj.put("result", obj.getJSONObject("result"));
        obj.put("command", "update-map");
        System.out.println("Update map to send: "+ obj.toString());
        return obj.toString();
    }

    /**
     * Obtiene una cadena codificada en JSON para moverse.
     * @param mov Movimiento del agente (ver el enum Movimientos)
     * @see Movimientos.java
     * @return String en formato JSON con el movimiento especificado.
     * @author Dani
     */
    public String encodeMove(Movimientos mov){
        JSONObject obj = new JSONObject();
        obj.put("command", mov);
        return obj.toString();
    }
    
    /**
     * Obtiene una cadena codificada en JSON para recargar batería.
     * @return String en formato JSON.
     * @author Dani
     */
    public String encodeRefuel(){
        JSONObject obj = new JSONObject();
        obj.put("command", Movimientos.refuel);
        return obj.toString();
    }
    
    /**
     * Decodifica un string JSON con la lectura del radar y lo devuelve
     * en un array.
     * @param json String JSON con la lectura del radar
     * @return ArrayList con los valores del radar
     * @author Dani
     */
    public ArrayList<Integer> decodeRadar(String json){
        JSONObject obj = new JSONObject(json);
        JSONArray array = obj.getJSONObject("result").getJSONArray("sensor");
        
        ArrayList<Integer> radar = new ArrayList();
        for (Object o : array)
            radar.add((Integer)o);
        
        return radar;
    }
    
    /**
     * Decodifica un string JSON con la lectura de la batería y 
     * lo devuelve.
     * @param json String JSON con la lectura de la batería.
     * @return int con la lectura de la batería.
     * @author Dani
     */
    public int decodeBattery(String json){
        JSONObject obj = new JSONObject(json);
        return (obj.getJSONObject("result").getInt("battery"));
    }
    
    /**
     * Decodifica un string JSON con la lectura del nivel global de energía y 
     * lo devuelve.
     * @param json String JSON con la lectura del nivel global de energía.
     * @return int con el nivel global de la energía.
     * @author Dani
     */
    public int decodeEnergy(String json){
        JSONObject obj = new JSONObject(json);
        return (obj.getJSONObject("result").getInt("energy"));
    }
    
    /**
     * Decodifica un string JSON con la lectura de si el agente está en
     * el objetivo.
     * @param json String JSON con la lectura de si el agente está en el objetivo.
     * @return true si está en el objetivo, false si no lo está.
     * @author Dani
     */
    public boolean decodeGoal(String json){
        JSONObject obj = new JSONObject(json);
        return (obj.getJSONObject("result").getBoolean("goal"));
    }
    
    /**
     * Decodifica un string JSON con la lectura del GPS y 
     * lo devuelve como un objeto Point
     * @param json String JSON de la lectura del GPS
     * @return Objeto Point con los valores de x e y
     * @author Dani y Emilien
     */
    public Posicion decodeGPS(String json){
        JSONObject obj = new JSONObject(json);

        int x = obj.getJSONObject("result").getInt("x");
        int y = obj.getJSONObject("result").getInt("y");
        
        return (new Posicion(x,y));
    }
    
    /**
     * @author Emilien Giard
     * Decodifica un string JSON con la command de un vehiculo
     * por el AgenteMapa y lo devuelve.
     * @param json String JSON con la command.
     * @return String de la command.
     */
    public String decodeCommandVehiculo(String json){
        JSONObject obj = new JSONObject(json);
        return (obj.getString("command"));
    }

    /**
     * @author Emilien Giard
     * Decodifica un string JSON con los percepciones de un vehiculo
     * por el AgenteMapa y lo devuelve.
     * @param json String JSON con los percepciones.
     * @return matriz de los percepciones.
     */
    public Integer[][] decodePercepciones(String json){
        JSONObject obj = new JSONObject(json);
        JSONArray array = obj.getJSONArray("percepciones");
        // get the number of case of the perceptions' side
        int length = array.length() / array.length();
        Integer[][] percepciones = new Integer[length][length];
        int i = 0, j = 0;
        for (Object o : array) {
            percepciones[i][j] = (Integer)o;
            i ++;
            // at the end of a line, go to the first column of the next line
            if (i == (length - 1)){
                i = 0;
                j++;
            }
        }

        return percepciones;
    }

    /**
     * Crea una imagen .png a partir de una traza de datos en una cadena
     * JSON.
     * @param traza Cadena JSON de la traza.
     * @param nombre_fichero Nombre del fichero creado. Recordar extension .png
     * @author Dani
     */
    public void guardarTraza(String traza, String nombre_fichero){

        try {
            JSONObject obj = new JSONObject(traza);
            List<Object> lista = obj.getJSONArray("trace").toList();
            
            byte data[] = new byte[lista.size()];
            for (int i=0; i<data.length; i++)
                data[i] = (byte) ((int)lista.get(i));
            
            FileOutputStream fos = new FileOutputStream(nombre_fichero);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public String encodeConfirmacionCheckin() {
        JSONObject obj = new JSONObject();
        obj.put("command", "checked-in");
        return obj.toString();
    }
}
