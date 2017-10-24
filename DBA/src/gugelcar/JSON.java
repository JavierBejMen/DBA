            /* Crear librería con el siguiente JAR para obtener la funcionalidad de la
biblioteca org.json: 

http://mvnrepository.com/artifact/org.json/json/20160810
*/

package gugelcar;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Daniel Díaz Pareja
 * @brief Clase encargada de obtener los atributos necesarios de un String
 * codificado en formato JSON o de codificar strings para enviar al servidor.
 */
public class JSON {
    
    public JSON(){}
    
    /**
     * @brief Crea el string para el logeo con el controlador. Si hay algún 
     * sensor que no se quiere utilizar se pondrá el nombre del agente como la 
     * cadena vacía.
     * @param world Mundo al que conecarse
     * @param agente_radar Nombre del agente tiene el radar
     * @param agente_scanner Nombre del agente que tiene el escáner
     * @param agente_battery Nombre del agente que tiene la batería
     * @param agente_gps Nombre del agente que tiene el gps
     * @return String para el logeo 
     */
    public String encodeLoginControlador(String world, String agente_radar, 
            String agente_scanner, String agente_battery, String agente_gps)
    {
        JSONObject obj = new JSONObject();
        
        obj.put("command", "login");
        obj.put("world", world);
        
        if (!agente_radar.isEmpty())
            obj.put("radar", agente_radar);
        
        if (!agente_scanner.isEmpty())
            obj.put("scanner", agente_scanner);
        
        if (!agente_battery.isEmpty())
            obj.put("battery", agente_battery);
        
        if (!agente_gps.isEmpty())
            obj.put("gps", agente_gps);

        return obj.toString();
    }
    
    /**
     * @brief Obtiene el estado del controlador a partir de una cadena JSON con el
     * campo "result"
     * @param cadena_json cadena JSON
     * @return Estado del controlador. Ver posibles valores en Estados.java
     * @see gugelcar.Estados
     */
    public Estados decodeEstado(String cadena_json){
        Estados e = null;
        JSONObject obj = new JSONObject(cadena_json);
        
        String estado = (String)obj.get("result");
        
        switch (estado) {
            case "OK": e = Estados.OK; break;
            case "CRASHED": e = Estados.CRASHED; break;
            case "BAD_COMMAND": e = Estados.BAD_COMMAND; break;
            case "BAD_PROTOCOL": e = Estados.BAD_PROTOCOL; break;
            case "BAD_KEY": e = Estados.BAD_KEY; break;    
        }
        
        return e;
    }
    
    /**
     * @brief Obtiene una cadena codificada en JSON para recargar batería.
     * @param key Password obtenido en el login del agente
     * @return Cadena codificada.
     */
    public String encodeRefuel(String key){
        JSONObject obj = new JSONObject();
        
        obj.put("command", "refuel");
        obj.put("key", key);
        
        return obj.toString();
    }
    
    /**
     * @brief Obtiene una cadena codificada en JSON para moverse.
     * @param mov Movimiento del agente
     * @param key Password obtenido en el login del agente
     * @return Cadena codificada.
     */
    public String encodeMove(Movimientos mov, String key){
        JSONObject obj = new JSONObject();
        
        obj.put("command", mov.toString());
        obj.put("key", key);
        
        return obj.toString();
    }
    
    /**
     * @brief Obtiene una cadena codificada en JSON para terminar la sesión.
     * @param key Password obtenido en el login del agente
     * @return Cadena codificada.
     */
    public String encodeLogout(String key){
        JSONObject obj = new JSONObject();
        
        obj.put("command", "logout");
        obj.put("key", key);
        
        return obj.toString();
    }
    
    /**
     * @brief Decodifica un string JSON con la lectura del escaner y lo devuelve
     * en un array.
     * @param lectura String JSON de la lectura del escaner
     * @return Array con los valores del escaner
     */
    public ArrayList<Float> decodeScanner(String lectura){
        ArrayList<Float> resultado = new ArrayList();
        
        JSONObject obj = new JSONObject(lectura);
        JSONArray array = obj.getJSONArray("scanner");
        
        for (Object o : array)
            resultado.add(Double.valueOf((Double)o).floatValue());
        
        return resultado;
    }
    
    /**
     * @brief Decodifica un string JSON con la lectura de la batería y lo devuelve
     * en un array.
     * @param lectura String JSON de la lectura del radar
     * @return Array con los valores del radar
     */
    public ArrayList<Integer> decodeRadar(String lectura){
        ArrayList<Integer> resultado = new ArrayList();
        
        JSONObject obj = new JSONObject(lectura);
        JSONArray array = obj.getJSONArray("radar");
        
        for (Object o : array)
            resultado.add((Integer)o);
        
        return resultado;
    }
    
    /**
     * @brief Decodifica un string JSON con la lectura de la batería y 
     * lo devuelve.
     * @param lectura String JSON de la lectura de la batería.
     * @return Entero con la lectura de la batería.
     */
    public int decodeBattery(String lectura){
        JSONObject obj = new JSONObject(lectura);
        
        return obj.getInt("battery");
    }
    
    /**
     * @brief Decodifica un string JSON con la lectura del GPS y 
     * lo devuelve como un objeto Point
     * @param lectura String JSON de la lectura del GPS
     * @return Objeto Point con los valores de x e y
     */
    public Point decodeGPS(String lectura){
        JSONObject obj = new JSONObject(lectura);
        JSONObject coordenadas = obj.getJSONObject("gps");

        int x = coordenadas.getInt("x");
        int y = coordenadas.getInt("y");
        
        return (new Point(x,y));
    }
    
    /**
     * @brief Crea una imagen .png a partir de una traza de datos en una cadena
     * JSON.
     * @param traza Cadena JSON de la traza.
     * @param nombre_fichero Nombre del fichero creado. Recordar extension .png
     * @todo Probar funcionalidad
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
