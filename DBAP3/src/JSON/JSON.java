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

/**
 * @author Dani
 * @brief Clase encargada de obtener los atributos necesarios de un String
 * codificado en formato JSON o de codificar strings para enviar al servidor.
 */
public class JSON {
    
    public JSON(){}
    
    /**
     * @author Dani
     * @brief Crea el string JSON con el mundo especificado. Lo utilizamos en
     * el primer SUBSCRIBE.
     * @param world Mundo de la cadena JSON
     * @return string JSON con el mundo especificado
     */
    public String encodeWorld(String world)
    {
        JSONObject obj = new JSONObject();
        obj.put("world", world);
        return obj.toString();
    }
    
    /**
     * @author Dani
     * @brief Obtiene la razón por la cual no se ha podido realizar 
     * alguna acción con el controlador, es decir, cuando responde FAILURE,
     * NOT_UNDERSTOOD, o REFUSE, con un JSON con el campo "details"
     * @param json cadena JSON que devuelve el servidor al producirse un 
     * error
     * @return Razón por la cual no se ha podido realizar la acción.
     */
    public String decodeErrorControlador(String json){
        JSONObject obj = new JSONObject(json);
        String razon = (String)obj.get("details");
        return razon;
    }
    
    /**
     * @author Dani
     * @brief Crea el string JSON para el checkin.
     * @return string JSON para el checkin
     */
    public String encodeCheckin(){
        JSONObject obj = new JSONObject();
        obj.put("command", "checkin");
        return obj.toString();
    }
    
    /**
     * @author Dani
     * @brief Decodifica un string JSON con las capabilities del agente. Devuelve
     * un ArrayList de objetos en este orden: 1º-fuelrate (int), 2º-range (int),
     * 3º-fly (boolean)
     * @param json cadena JSON con las capabilities
     * @return ArrayList de objetos en el orden especificado.
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
    public String encondeAgentParam(AgenteVehiculo vehiculo){
        JSONObject obj = new JSONObject();
        obj.put("tipo", vehiculo.getTipo());
        obj.put("bateria", vehiculo.getBateria());
        obj.put("range", vehiculo.getRange());
        obj.put("fuelrate", vehiculo.getFuelrate());
        obj.put("fly", vehiculo.getFly());
        
        return obj.toString();
    }
    
    /**
     * @author Dani
     * @brief Obtiene una cadena codificada en JSON para moverse.
     * @param mov Movimiento del agente (ver el enum Movimientos)
     * @see Movimientos.java
     * @return String en formato JSON con el movimiento especificado.
     */
    public String encodeMove(Movimientos mov){
        JSONObject obj = new JSONObject();
        obj.put("command", mov);
        return obj.toString();
    }
    
    /**
     * @author Dani
     * @brief Obtiene una cadena codificada en JSON para recargar batería.
     * @return String en formato JSON.
     */
    public String encodeRefuel(){
        JSONObject obj = new JSONObject();
        obj.put("command", Movimientos.refuel);
        return obj.toString();
    }
    
    /**
     * @author Dani
     * @brief Decodifica un string JSON con la lectura del radar y lo devuelve
     * en un array.
     * @param json String JSON con la lectura del radar
     * @return ArrayList con los valores del radar
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
     * @author Dani
     * @brief Decodifica un string JSON con la lectura de la batería y 
     * lo devuelve.
     * @param json String JSON con la lectura de la batería.
     * @return int con la lectura de la batería.
     */
    public int decodeBattery(String json){
        JSONObject obj = new JSONObject(json);
        return (obj.getJSONObject("result").getInt("battery"));
    }
    
    /**
     * @author Dani
     * @brief Decodifica un string JSON con la lectura del nivel global de energía y 
     * lo devuelve.
     * @param json String JSON con la lectura del nivel global de energía.
     * @return int con el nivel global de la energía.
     */
    public int decodeEnergy(String json){
        JSONObject obj = new JSONObject(json);
        return (obj.getJSONObject("result").getInt("energy"));
    }
    
    /**
     * @author Dani
     * @brief Decodifica un string JSON con la lectura de si el agente está en
     * el objetivo.
     * @param json String JSON con la lectura de si el agente está en el objetivo.
     * @return true si está en el objetivo, false si no lo está.
     */
    public boolean decodeGoal(String json){
        JSONObject obj = new JSONObject(json);
        return (obj.getJSONObject("result").getBoolean("goal"));
    }
    
    /**
     * @author Dani
     * @brief Decodifica un string JSON con la lectura del GPS y 
     * lo devuelve como un objeto Point
     * @param json String JSON de la lectura del GPS
     * @return Objeto Point con los valores de x e y
     */
    public Point decodeGPS(String json){
        JSONObject obj = new JSONObject(json);

        int x = obj.getJSONObject("result").getInt("x");
        int y = obj.getJSONObject("result").getInt("y");
        
        return (new Point(x,y));
    }
    
    /**
     * @author Emilien Giard
     * @brief Decodifica un string JSON con la command de un vehiculo
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
     * @brief Decodifica un string JSON con los percepciones de un vehiculo
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
     * @author Dani
     * @brief Crea una imagen .png a partir de una traza de datos en una cadena
     * JSON.
     * @param traza Cadena JSON de la traza.
     * @param nombre_fichero Nombre del fichero creado. Recordar extension .png
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
            System.out.println("Traza guardada");
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
