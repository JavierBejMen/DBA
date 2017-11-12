/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gugelcar;
import gugelcar.exceptions.*;

/**
 * @brief Clase para representar un punto
 * @author Javier Bejar Mendez
 */
public class Posicion {
    private Integer x;
    private Integer y;
    
    //Constructores
    public Posicion(){
        this.x = null;
        this.y = null;
    }
    public Posicion(Integer x, Integer y){
        this.x = x;
        this.y = y;
    }
    public Posicion(Posicion otro){
        this.x = otro.x;
        this.y = otro.y;
    }
    
    //Devuelve la accion para ir desde ESTA posicion a OTRO
    public Movimientos getMove(Posicion otro){
        Cardinal card= new Cardinal(this.getCardinal(otro));
        Movimientos move = null;
        try{
            switch(card.get()){
                case "n":
            move = Movimientos.moveN;
            break;
        case "s"  :
            move = Movimientos.moveS;
            break;
        case "o":
            move = Movimientos.moveW;
            break;
        case "e":
            move = Movimientos.moveE;
            break;
        case "no":
            move = Movimientos.moveNW;
            break;
        case "ne":
            move = Movimientos.moveNE;
            break;
        case "so"  :
            move = Movimientos.moveSW;
            break;
        case "se"  :
            move = Movimientos.moveSE;
            break;
        default:
            System.out.println("Algo fue mal al obtener el movimiento");
            }
        }catch(ExceptionNonInitialized ex){
            System.out.println("Error al calcular movimiento: "+ex.getMessage());
        }
        return move;
    }
    //Obtener la cardinalidad de ESTE punto respecto a OTRO
    public Cardinal getCardinal(Posicion otro){
        
        String cardinal="";
        if(this.y < otro.y){
            cardinal += "n";
        }else if(this.y > otro.y){
            cardinal += "s";
        }
    
        if(this.x < otro.x){
            cardinal += "o";
        }else if(this.x > otro.x){
            cardinal += "e";
        }
        Cardinal card = new Cardinal();
        try{
            card.set(cardinal);
        }catch(ExceptionBadParam ex){
            System.out.println("Error al obtener la cardinalidad entre dos posiciones: "+ex.getMessage());
        }
        return card;
    }
    
    //asignacion
    public void asign(Posicion otro){
        this.x = otro.x;
        this.y = otro.y;
    }
    
    //Comparador de igualdad
    public boolean equals(Posicion otro){
        return this.x == otro.x && this.y == otro.y;
    }
    
    //Getters y Setters
    public Integer getX() throws ExceptionNonInitialized{
        if(this.x == null){
            throw new ExceptionNonInitialized("Intentando acceder(get) a una posición no inicializada (X)");
        }
        return this.x;
    }
    public Integer getY() throws ExceptionNonInitialized{
        if(this.y == null){
            throw new ExceptionNonInitialized("Intentando acceder(get) a una posición no inicializada (Y)");
        }
        return this.y;
    }
    public void setX(Integer x){
        this.x = x;
    }
    public void setY(Integer y){
        this.y = y;
    }
}
