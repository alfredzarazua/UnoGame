/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package serveruno;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author anahi
 */

public class Baraja {
    private Carta[] juegoCartas;

    public Baraja(){
        juegoCartas = new Carta[62];  
        int cont=0;
        String[] colores=new String[] {"Rojo", "Verde","Amarillo", "Azul"};
        
        for(int i=0; i<4; i++){
            for(int j=0; j<10;j++){
                juegoCartas[cont]= new Carta(colores[i],1, j);// clave vacia 40
                cont++;
                //10
            }
            
            juegoCartas[cont]= new Carta(colores[i],0 ,10 ); //cartaReversa;
            cont++;
            juegoCartas[cont]= new Carta(colores[i],0 ,11); //cartaSum2
            cont++;
            juegoCartas[cont]= new Carta(colores[i],0 ,12 ); //cartaBloqueo;
               // clave vacia 52 
            cont++;//13
        }
        juegoCartas[cont]= new Carta("Negro",0 ,13); //52
        cont++;
         juegoCartas[cont]= new Carta("Negro",0 ,14);//53
        cont++;
        
        for(int i=0; i<4; i++){
           juegoCartas[cont]= new Carta(colores[i],0 ,13); // 58 vacia;
           cont++;
        }
        
        for(int i=0; i<4; i++){
           juegoCartas[cont]= new Carta(colores[i],0 ,14); // 62 vacia;
           cont++;
        } 
        //vacia 62
    }

     
     public Carta[] getJuegoCartas(){
         return juegoCartas;
     }
    
}


