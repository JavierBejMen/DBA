/* Crear librería con el siguiente JAR para obtener la funcionalidad de la
biblioteca org.json: 

http://mvnrepository.com/artifact/org.json/json/20160810
*/

package JSON;
import gugelcar.Movimiento;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import gugelcar.Mapa;
import gugelcar.Posicion;
import gugelcar.exceptions.ExceptionBadParam;
import gugelcar.exceptions.ExceptionNonInitialized;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import org.json.JSONException;

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
    /*public String encodeAgentParam(AgenteVehiculo vehiculo){
        JSONObject obj = new JSONObject();
        obj.put("tipo", vehiculo.getTipo());
        obj.put("bateria", vehiculo.getBateria());
        obj.put("range", vehiculo.getRange());
        obj.put("fuelrate", vehiculo.getFuelrate());
        obj.put("fly", vehiculo.getFly());
        
        return obj.toString();
    }*/
    
    /**
     * Codifica en JSON los lo que vé el vehículo actualmente y su posición
     * @param vision Lo que el vehículo ve actualmente. Por ejemplo, si es un radar,
     * será un ArrayList de 3x3.
     * @param pos Posicion actual del vehículo
     * @return String en formato JSON con las percepciones del vehiculo
     * @author Emilien Giard, Dani
     */
    public String encodeUpdateMap(ArrayList<Integer> vision, Posicion pos, boolean obj_enc){
        JSONObject obj = new JSONObject();
        try {
            obj.put("command", "update-map");
            obj.put("vision",vision);
            obj.put("x", pos.getX());
            obj.put("y", pos.getY());
            obj.put("objetivo_encontrado", obj_enc);
        } catch (ExceptionNonInitialized ex) {
            System.out.println("Excepción en encodeUpdateMap(). Mensaje "+ex.getMessage());
        }
        return obj.toString();
    }

    /**
     * Obtiene una cadena codificada en JSON para moverse.
     * @param mov Movimiento del agente (ver el enum Movimiento)
     * @see Movimientos.java
     * @return String en formato JSON con el movimiento especificado.
     * @author Dani
     */
    public String encodeMove(Movimiento mov){
        JSONObject obj = new JSONObject();
        obj.put("command", mov.toString());
        return obj.toString();
    }
    
    /**
     * Obtiene una cadena codificada en JSON para recargar batería.
     * @return String en formato JSON.
     * @author Dani
     */
    public String encodeRefuel(){
        JSONObject obj = new JSONObject();
        obj.put("command", "refuel");
        return obj.toString();
    }
    
    /**
     * Decodifica un string JSON con la lectura del radar y lo devuelve
     * en un JSONArray.
     * @param json String JSON con la lectura del radar
     * @return JSONArray con los valores del radar
     * @author Dani
     */
    public JSONArray decodeRadar(String json){
        JSONObject obj = new JSONObject(json);
        JSONArray array = obj.getJSONObject("result").getJSONArray("sensor");
        return array;
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
     * Decodifica un string JSON con las percepciones y 
     * lo devuelve como un objeto Point
     * @param json String JSON de la lectura del GPS
     * @return Objeto Posicion con los valores de x e y
     * @author Dani, Emilien
     */
    public Posicion decodeGPS(String json){
        JSONObject obj = new JSONObject(json);

        int x = obj.getJSONObject("result").getInt("x");
        int y = obj.getJSONObject("result").getInt("y");
        
        return (new Posicion(x,y));
    }
    
    /**
     * Decodifica un string JSON con los campos "x" e "y" y 
     * lo devuelve como un objeto Posicion
     * @param json String JSON de la lectura del GPS
     * @return Objeto Posicion con los valores de x e y
     * @author Dani
     */
    public Posicion decodePos(String json){
        JSONObject obj = new JSONObject(json);

        int x = obj.getInt("x");
        int y = obj.getInt("y");
        
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
     * @author Emilien Giard, Dani
     * Decodifica un string JSON con el campo "mapa" 
     * y el campo "tam", donde tam es el tamaño de la fila,
     * y lo devuelve en un objeto Mapa.
     * @param json String JSON con el mapa y el tamaño
     * @return Mapa a partir del string JSON con el campo "mapa" y "tam"
     */
    public Mapa decodeMapa(String json){
        Mapa mapa = null;
        
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray array = obj.getJSONArray("mapa");
            int tam = obj.getInt("tam");
            Integer[][] map = new Integer[tam][tam];
            
            for (int fil = 0; fil < tam; fil++)
                for (int col = 0; col < tam; col++)
                    map[fil][col] = (Integer)array.getInt(fil*tam+col);
            
            mapa = new Mapa(1);
            mapa.setTam(tam);
            mapa.setMapa(map);
        } catch (ExceptionBadParam ex) {
            System.out.println("Excepcion en decodeMapa(). Mensaje: "+ex.getMessage());
        }
        
        return mapa;
    }
    
    /**
     * Codifica un mapa, el tamaño del mismo y si se ha encontrado el objetivo
     * en un string json, con el formato
     * {"mapa":[...], "tam":..., "objetivo_encontrado":...}
     * @author Dani
     * @param m Mapa en cuestión
     * @param obj_enc Booleano con si se ha encontrado el objetivo o no
     * @return String json con el formato {"mapa":[...], "tam":..., "objetivo_encontrado":...}
     */
    public String encodeMapa(Mapa m, boolean obj_enc){
        JSONObject obj = new JSONObject();
        int tam = m.getTam();
        obj.put("objetivo_encontrado", obj_enc);
        obj.put("tam", tam);
        JSONArray array = new JSONArray();
        Integer[][] matriz = new Integer[tam][tam];
        matriz = m.mapToMatrix();
        for (int fil = 0; fil < tam; fil++)
            for (int col = 0; col < tam; col++)
                array.put(matriz[fil][col]);
        obj.put("mapa",array);
        return obj.toString();
    }
    
    /**
     * Decodifica el tamaño de un mapa (tamaño de fila) pasado por el parámetro
     * "tam" en una cadena json.
     * @param json Cadena json con el parámetro "tam"
     * @return entero con el tamaño
     * @author Dani
     */
    public int decodeTam(String json){
        JSONObject obj = new JSONObject(json);
        return obj.getInt("tam");
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
    
    /**
     * Exporta el mapa explorado en la iteración n
     * @param map mapa que vamos a exportar
     * @param encontrado boolean que indica si el objetivo ha sido encontrado
     * @param iteracion número de la iteración en la que nos encontramos

     * @author Jorge, Dani
     */
    public void exportMapa(Mapa map, boolean encontrado, int iteracion, String nombre_mapa){
        JSONObject obj = new JSONObject();
        try{
            int tam = map.getTam();
            obj.put("iteracion", iteracion);
            obj.put("encontrado", encontrado);
            obj.put("tamanio", tam);
            JSONArray array = new JSONArray();
            for (int fil = 0; fil < tam; fil++)
                for (int col = 0; col < tam; col++)
                    array.put(map.get(fil,col));
            
            obj.put("mapa",array);

            File archivo = new File(nombre_mapa+".json");
            FileWriter escribir=new FileWriter(archivo,false); 
            escribir.write(obj.toString());
            escribir.close();
            
        //Si existe un problema al escribir cae aqui
        } catch(JSONException | IOException | ExceptionBadParam e){
            System.out.println("Error al exportar el mapa: "+e.getMessage());
        }
    }

        
    /**
     * importa el mapa explorado en la iteración n
     * @return JSONObject con el contenido de la iteración anterior
     * @author Jorge,Dani
     */
    public JSONObject importMapa(String nombre_mapa){
        JSONObject obj = null;
        try{
            String cadena;
            File archivo = new File(nombre_mapa+".json");
            FileReader f = new FileReader(archivo);
            BufferedReader b = new BufferedReader(f);
            String aux = "";
            while((cadena = b.readLine())!=null)
                aux = cadena;
            
            b.close();

            obj = new JSONObject(aux);

        } catch(Exception e){
            System.out.println("Exepción en importMapa() de la clase JSON: "+e.getMessage());
        }
        
        return obj;
        
    }
    /**
     * @author Dani
     * @return 
     */
    public String encodeConfirmacionCheckin() {
        JSONObject obj = new JSONObject();
        obj.put("command", "checked-in");
        return obj.toString();
    }
    
    /**
     * @author Dani
     * @param json
     * @return 
     */
    public JSONArray decodeVision(String json) {
        JSONArray array = new JSONArray();
        try {
            JSONObject obj = new JSONObject(json);
            array = obj.getJSONArray("vision");
        } catch (Exception ex) {
            System.out.println("Excepcion en decodeVision(): "+ex.getMessage());
        }
        return array;
    }
}