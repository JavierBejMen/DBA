/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gugelcar.exceptions;

/**
 * @brief Lanza una excepción si tratamos de hacer un set a una posicion no inicializada
 * @author Javier Bejar Mendez
 */
public class ExceptionNonInitialized extends Exception{
    public ExceptionNonInitialized(String msg){
        super(msg);
    }
}
