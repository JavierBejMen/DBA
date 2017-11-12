/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gugelcar;
import gugelcar.exceptions.*;

/**
 *
 * @author Javier Bejar Mendez
 */
public class Mapa {
    private Integer[][] mapa;
    private Integer TAM;
    
    //Constructor
    public Mapa(Integer tam) throws ExceptionBadParam{
        if(tam < 0){
            throw new ExceptionBadParam("Error al construir mapa, parametro menores que 0");
        }
        this.TAM = tam;
        this.mapa = new Integer[TAM][TAM];
        for(int i = 0; i < tam; ++i){
            for(int j = 0; j < tam; ++j){
                this.mapa[i][j] = 0;
            }
        }
        
    }
    
    //Obtiene la posicion asociada a un vector relativa al parámetro pos
    public Posicion getFromVector(Posicion pos, Integer xy, Integer n){
        Integer x = null;
        Integer y = null;
        Integer tam = (int)Math.sqrt(n);
        Integer mid = tam/2;
        try{
            x = pos.getX();
            y = pos.getY();
        }catch(ExceptionNonInitialized ex){
            System.out.println("Error al acceder al mapa a traves de vector: "+ex.getMessage());
        }
        x -= mid + xy%tam;
        y = mid + xy/tam;
        
        pos.setX(x);
        pos.setY(y);
        
        return pos;
    }
    
    //Getters y Setters
    public Integer get(Posicion pos) throws ExceptionBadParam{
        
        Integer x = null;
        Integer y = null;
        try{
            x = pos.getX();
            y = pos.getY();
            if(pos.getX() >= this.TAM || pos.getY() >= this.TAM){
                throw new ExceptionBadParam("Error al acceder al mapa, posicion fuera de rango");
            }
        }catch (ExceptionNonInitialized ex){
            System.out.println("Error, se ha pasado como parámetro a mapa: "+ex.getMessage());
        }
        
        return this.mapa[x][y];
    }
    public Integer get(Integer x, Integer y) throws ExceptionBadParam{
        if(x >= this.TAM || y >= this.TAM || x < 0 || y < 0){
            throw new ExceptionBadParam("Error al acceder al mapa, parámetros fuera de rango");
        }
        
        return this.mapa[x][y];
    }
    public void set(Posicion pos, Integer value) throws ExceptionBadParam{
        Integer x = null;
        Integer y = null;
        
        try{
            x=pos.getX();
            y=pos.getY();
        }catch(ExceptionNonInitialized ex){
            System.out.println("Error al intentar modificar mapa: "+ex.getMessage());
        }
        if(x >= this.TAM || y >= this.TAM || x < 0 || y < 0){
            throw new ExceptionBadParam("Error al modificar el mapa, parámetros fuera de rango");
        }
        this.mapa[x][y] = value;
    }
    
    //Imprime el mapa por consola
    public void print(){
        for(int i = 0;i < this.TAM; ++i){
            System.out.println("row:"+i);
            for(int j = 0;j < this.TAM; ++j){
                System.out.format("%1$+020 %i   ",this.mapa[i][j]);
            }
        }
    }
    public void print(Posicion pos, Integer l)throws ExceptionBadParam{
        Integer x = null;
        Integer y = null;
        try{
            x = pos.getX();
            y = pos.getY();
        }catch(ExceptionNonInitialized ex){
            System.out.println("Error al imprimir mapa: "+ex.getMessage());
        }
        if((x-l < 0 || x+l>this.TAM) || (y-l < 0 || y+l>this.TAM)){
            throw new ExceptionBadParam("Error al imprimir mapa, se sale de rango");
        }
        for(int i = x-l; i < l+x; ++i){
             System.out.println("row:"+i);
            for(int j = y-l; j < l+y; ++j){
                System.out.format("%1$+020 %i   ",this.mapa[i][j]);
            }
        }
    }
    
}

