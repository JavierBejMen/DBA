/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Alejandro;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import static java.lang.Math.sqrt;
import java.util.ArrayList;

/**
 *
 * @author donas11
 */
public class Agente extends SingleAgent{
    private String Id="";
    private String Replywith="";
    private TipoAgente Tipo;
    private boolean enobjetivo=false;
    private boolean cercaobjetivo=false;
    private int x;
    private int y;
    private int objetivox;
    private int objetivoy;
    private double distanciaabjetivo;
    private ArrayList<Integer> lectura_radar;
    
    

    public Agente(AgentID aid) throws Exception {
        super(aid);
    }
    
    public int getx(){
        return x;
    }
    public int gety(){
        return y;
    }
    private int posMatriz(int fil,int col,int numplanos){
          return numplanos*fil+col;
    }
    
        
    
    private void calculardistanciaaobjetivo(){
        distanciaabjetivo=sqrt(((objetivox -x)*(objetivox -x))+((objetivoy- y)*(objetivoy- y)));
    }
    
    public boolean cercadeobjetivo (){
        for(int i=0;i<lectura_radar.size();i++ ){
            if (lectura_radar.get(i)==3){
                return true;
            }
        }
        return false;
    
    }
    
    public void Calcularobjetivo(){
        
        switch (Tipo) {
            case CAR: 
                
            break;
            case DRONE: 
                
            break;
            case TRUCK: 
                
            break;
            case STAY:
                
            break;
            
        }
        
    }
    
    
    public double CalculardistanciaaotroAgente(Agente agente){
        double dx=(agente.getx() -x);
        double dy=(agente.gety()- y);
        double devolver=0;
        switch (Tipo) {
            case CAR: 
                devolver=(sqrt( (dx*dx) +(dy*dy)));
            break;
            case DRONE: 
                devolver=(sqrt( (dx*dx) +(dy*dy)));
            break;
            case TRUCK: 
                devolver=(sqrt( (dx*dx) +(dy*dy)));
            break;
            case STAY:
                devolver=0.0;
            break;
            
        }
        return devolver;
    }
    
    
    
}
