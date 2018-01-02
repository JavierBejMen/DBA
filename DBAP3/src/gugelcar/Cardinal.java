/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gugelcar;
import gugelcar.exceptions.*;
/**
 * @brief clase que representa un punto cardinal
 * @author Javier Bejar Mendez
 */
public class Cardinal {
    private String point;
    private Integer value;
    
    //Comprueba que el valor del String es correcto
    private boolean checkCard(String card){
        boolean valido = (card.equals("n") || card.equals("s") || card.equals("o") || card.equals("e") ||
                card.equals("ne") || card.equals("no") || card.equals("se") || card.equals("so"));
        return valido;
    }
    //Calcula el valor segun el cardinal, para las comparaciones
    private void calcValue() throws ExceptionNonInitialized{
        switch(this.point){
        case "n":
            this.value = 0;
            break;
        case "s"  :
            this.value = 4;
            break;
        case "o":
            this.value = 6;
            break;
        case "e":
            this.value = 2;
            break;
        case "no":
            this.value = 7;
            break;
        case "ne":
            this.value = 1;
            break;
        case "so"  :
            this.value = 5;
            break;
        case "se"  :
            this.value = 3;
            break;
        default:
            throw new ExceptionNonInitialized("Error al llamar a calcValue() en "
                    + "cardinal, cardinal no inicializado o inicializado incorrectamente");
        }
    }
    
    //Constructores
    public Cardinal(){
        this.point = null;
        this.value = null;
    }
    public Cardinal(String card) throws ExceptionBadParam{
        if(!this.checkCard(card)){
            throw new ExceptionBadParam("Error al construir cardinal"
                    + "con valor: "+card);
        }
        else{
            this.point = card;
            try{
                this.calcValue();
            }
            catch (ExceptionNonInitialized ex){
                System.out.println(ex.getMessage());
            }
        }
    }
    public Cardinal(Cardinal otro){
        this.point = otro.point;
        this.value = otro.value;
    }
    
    //Asignacion
    public void asign(Cardinal otro){
        this.point = otro.point;
        this.value = otro.value;
    }
    
    //El cardinal se iguala al siguiente
    private void next() throws ExceptionNonInitialized{
        try{
            switch(this.point){
                case "n":
                    this.point = "ne";
                    this.calcValue();
                    break;
                case "s"  :
                    this.point = "so";
                    this.calcValue();
                    break;
                case "o":
                    this.point = "no";
                    this.calcValue();
                    break;
                case "e":
                    this.point = "se";
                    this.calcValue();
                    break;
                case "no":
                    this.point = "n";
                    this.calcValue();
                    break;
                case "ne":
                    this.point = "e";
                    this.calcValue();
                    break;
                case "so"  :
                    this.point = "o";
                    this.calcValue();
                    break;
                case "se"  :
                    this.point = "s";
                    this.calcValue();
                    break;
                default:
                    throw new ExceptionNonInitialized("Cardinal no inicializado al intentar next()");
            }
        }catch(ExceptionNonInitialized ex){
            System.out.println(ex.getMessage());
        }
    
    }
    //Comparaciones
    //Igualdad
    public boolean equals(Cardinal otro){
        return this.value == otro.value;
    }
    //Menor
    public boolean les(Cardinal otro){
        return this.value < otro.value;
    }
    //Menor o igual
    public boolean leseq(Cardinal otro){
        return this.value <= otro.value;
    }
    
    //Setters getters
    public String get()throws ExceptionNonInitialized{
        if(this.point==null){
            throw new ExceptionNonInitialized("Error al acceder(get) a cardinal no inicializado");
        }
        return this.point;
    }
    public void set(String card) throws ExceptionBadParam{
        if(!this.checkCard(card)){
            throw new ExceptionBadParam("Error al asignar cardinal"
                    + "con valor: "+card);
        }
        else{
            this.point = card;
            try{
                this.calcValue();
            }catch(ExceptionNonInitialized ex){
                System.out.println("Error en set:"+ex.getMessage());
            }
        }
    }
}
