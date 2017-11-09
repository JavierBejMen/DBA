/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gugelcar;

/**
 *
 * @author donas11
 */
public class Prueba implements Comparable<Prueba> {

        private int x;
        private int y;
        private float valor;
        private int veces;

        public Prueba(int x, int y, float valor,int veces) {
            this.x = x;
            this.y = y;
            this.valor = valor;
            this.veces = veces;
        }
        public float getvalor(){
            return valor;
        }
        public int getx(){
            return x;
        }
        public int gety(){
            return y;
        }
        public int getveces(){
            return veces;
        }
        public void setveces(int veces){
            this.veces = veces;
        }
        @Override
        public int compareTo(Prueba o) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            if (valor < o.valor) {
                return -1;
            }
            if (valor > o.valor) {
                return 1;
            }
            return 0;
        }

    
}
