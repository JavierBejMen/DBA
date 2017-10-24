    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gugelcar;

import java.awt.Point;
import java.util.ArrayList;
import static gugelcar.Estados.CRASHED;
/**
 *
 * @author 
 */
public class main {
    
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*
         * Prueba de la clase JSON.
         */
        JSON parser = new JSON();
        
        String login = parser.encodeLoginControlador("map1", "vehículo", 
                "vehículo", "vehículo", "");
        System.out.println("Prueba de codificación del login: " + login);
        
        Estados e = parser.decodeEstado("{\"result\":\"CRASHED\"}");
        System.out.println("Prueba de decodificación del estado: " + e);
        
        String comando = parser.encodeMove(Movimientos.moveE, 
                "password de prueba");
        System.out.println("Prueba comando move: " + comando);
        
        String lectura_escaner = "{\"scanner\": [75.00667,75.690155,76.38062,"
                + "77.07788,77.781746,75.74299,76.41989,"
                + "77.10383,77.7946,78.492035,76.48529,"
                + "77.155685,77.83315,78.51752,79.20859,"
                + "77.23341,77.89737,78.56844,79.24645,"
                + "79.93122,77.987175,78.64477,79.30952,"
                + "79.98125,80.65978]}";
        ArrayList<Float> array_scanner = parser.decodeScanner(lectura_escaner);
        System.out.println("Prueba array del scanner: " + array_scanner);
        
        String lectura_radar = "{\"radar\":[1,1,1,0,0,1,1,0,0,0,1,0,0,0,\n" +
        "0,0,0,0,0,0,0,0,0,0,0]}";
        ArrayList<Integer> array_radar = parser.decodeRadar(lectura_radar);
        System.out.println("Prueba array del radar: " + array_radar);
        
        String lectura_GPS = "{\"gps\":{\"x\":94, \"y\":94}}";
        Point punto = parser.decodeGPS(lectura_GPS);
        System.out.println("Prueba punto del GPS: (x=" + punto.x + ", y=" + punto.y + ")");
        

        GugelCar car = new GugelCar("test");
        car.login();
        while (car.estoyEnObjetivo() || car.getEstado_actual() == CRASHED) {
            car.actualizarMapa();
            String direcion = car.decidir();
            car.mover(direcion);
    }

    }
}
