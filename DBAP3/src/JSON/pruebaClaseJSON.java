/*
 * Realizado por: Daniel Díaz Pareja
 */
package JSON;

import gugelcar.Mapa;
import gugelcar.Movimientos;
import gugelcar.Posicion;
import gugelcar.exceptions.ExceptionBadParam;
import gugelcar.exceptions.ExceptionNonInitialized;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONObject;

/**
 * @author Dani
 */
public class pruebaClaseJSON {
    
    public static void main(String[] args) throws ExceptionBadParam, IOException {
        
        Mapa map = new Mapa(5);
        
        JSON parser = new JSON();
        JSONObject obj = parser.importMapa();
        System.out.println("Iteracion: " +obj.get("iteracion"));
        System.out.println("Encotrado: " +obj.get("encontrado"));
        System.out.println("Tamaño: " +obj.get("tamaño"));
        System.out.println("Mapa: " +obj.get("mapa"));


        parser.exportMapa(map, true, 1);

       
        // Encodes
        String world = parser.encodeWorld("world1");
        System.out.println("Prueba de codificación del world: " + world);
        
        String checkin = parser.encodeCheckin();
        System.out.println("Prueba de codificación del checkin: " + checkin);

        String move = parser.encodeMove(Movimientos.moveE);
        System.out.println("Prueba comando move: " + move);
        
        String refuel = parser.encodeRefuel();
        System.out.println("Prueba comando refuel: " + refuel);
        
        // Decodes
        String e = parser.decodeError("{\"details\":\"BAD MAP\"}");
        System.out.println("Prueba de decodificación del error: " + e);
        
        String radar = "\"sensor\":[1,1,1,0,0,1,1,0,0,0,1,0,0,0,\n" +
        "0,0,0,0,0,0,0,0,0,0,0], ";
        
        String sensores = "{\"result\":{"
                + "\"battery\":\"10\", "
                + "\"x\":\"99\", "
                + "\"y\":\"98\", "
                + radar
                + "\"energy\":\"10\", "
                + "\"goal\":\"false\", "
                + "}}";

        ArrayList<Integer> array_radar = parser.decodeRadar(sensores);
        System.out.println("Prueba array del radar: " + array_radar);

        Posicion punto = parser.decodeGPS(sensores);
        try {
            System.out.println("Prueba punto del GPS: (x=" + punto.getX() + ", y=" + punto.getY() + ")");
        } catch (ExceptionNonInitialized ex) {
            System.out.println(ex.getMessage());
        }
        
        int bateria = parser.decodeBattery(sensores);
        System.out.println("Prueba de la bateria: " + bateria);
        
        int energy = parser.decodeEnergy(sensores);
        System.out.println("Prueba de energy: " + energy);
        
        boolean goal = parser.decodeGoal(sensores);
        System.out.println("Prueba de goal: " + goal);
    }
}
