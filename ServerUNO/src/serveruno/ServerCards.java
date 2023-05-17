/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package serveruno;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.security.SecureRandom;


class CartaCom{
    int posicion;
    int cant;
    
    public CartaCom(int cant, int posicion){
        this.cant= cant; 
        this.posicion=posicion;
    }
    
    public int getPosicion(){
        return posicion;  
    }
    
    public int getCantidad(){
        return cant;  
    } 
    public void setCarta(int cant,int posicion){
    this.posicion= posicion; 
    this.cant= cant; 
}
    public void setPosicion(int posicion){
        this.posicion= posicion;
    }
    
    public void setCant(int cant){
        this.cant= cant; 
    }
    
    public void resCant( int cant){
        this.cant-=cant;
    }
}
           
public class ServerCards{
       List<CartaCom> comida;
       int[] userCards; 
    /*List<CartaCom> comida; 
    ServerCards cantCartas;*/
       
 private void inicializaUserCards(){
        for( int i=0; i<54; i++){
        userCards[i]=0;
        }
    }
    public ServerCards(){
        userCards= new int[54];
        comida = new ArrayList<>();
        
        inicializaCartasCant();
        inicializaUserCards();
        
    }
    
   
    
    public int darComida(){
        //checar si aun hay algo en el arreglo 
        int num;
        
        if(comida.isEmpty()){
            Barajar();
        }
        
        Random aleatorio = new Random(System.currentTimeMillis());
        // Producir nuevo int aleatorio entre 0 y cant elem del arreglo-1
        //int random= aleatorio.nextInt(comida.size());
        
        int random = (int)(Math.random()*comida.size()); // 0 
        
        System.err.println("random: "+random);
        num= comida.get(random).getPosicion();
        System.err.println("Carta: "+num);
        userCards[num]+=1;
        
        if( comida.get(random).getCantidad()-1==0){
            //remueva index
            comida.remove(random);
        }else{
            comida.get(random).resCant(1);
        }
        return num;      
    }
    
    public void regresaCartaUsuario(int num){
    userCards[num]-=1;
}
    
    private void Barajar(){
        inicializaCartasCant();
        //restan los q estan 
        int cont=0;
        while(cont!=comida.size()){
            if(comida.get(cont).getCantidad()-userCards[comida.get(cont).getPosicion()]==0){
            comida.remove(cont);
            }else{
                comida.get(cont).resCant(userCards[comida.get(cont).getPosicion()]);
                cont++;
            }
        }
    }
    private void inicializaCartasCant(){
      CartaCom c;
      //c.setCant(2);
      for( int i=0; i<52; i++){
          c= new CartaCom(2, i);
        //c.setPosicion(i);
        comida.add(c);
       }
      comida.get(0).setCant(1);
      comida.get(13).setCant(1);
      comida.get(26).setCant(1);
      comida.get(39).setCant(1);
      
      c= new CartaCom(4, 52);
      comida.add(c);
      c= new CartaCom(4, 53);
      comida.add(c);

      for( int i=0; i<comida.size(); i++){
           System.out.println(comida.get(i).getPosicion());
      }
    }
    
    
  

}
