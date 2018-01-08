package practica3;

/**
 * @brief Posibles movimientos del agente
 * @author Dani, Javier Bejar Mendez
 */
public enum Movimiento {
    moveN, moveS, moveE, moveW, moveNE, moveNW, moveSE, moveSW;
    
    /**
     * Devuelve un movimiento aleatorio. Método para hacer pruebas.
     * @author dani
     * @return Devuelve un movimiento aleatorio
     */
    public static Movimiento random(){
        int random = (int) (Math.random() * 8) + 1;
        Movimiento m = moveS;
        
        switch (random){
            case 1: m = moveN; break;
            case 2: m = moveS; break;
            case 3: m = moveE; break;
            case 4: m = moveW; break;
            case 5: m = moveNE; break;
            case 6: m = moveNW; break;
            case 7: m = moveSE; break;
            case 8: m = moveSW; break;
        }
        
        return m;
    }
}
