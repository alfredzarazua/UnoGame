
package serveruno;

/**
 *
 * @author anahi
 */
public class Carta{
    protected String color;
    protected int tipo;
    protected int simbolo;
    
    public String getColor(){
        return color;
    }
    
    public int getTipo(){
        return tipo;
    }  
    
    public int getSimbolo(){
    return simbolo;
}
    
    public Carta(String color, int tipo, int simbolo){
        this.color= color;
        this.tipo= tipo;
        this.simbolo=simbolo;
    }
    
    public void setCarta(String color, int tipo, int simbolo){
        this.color=color;
        this.simbolo= simbolo;
        this.tipo= tipo;
    }
    
    public boolean cartasCompatibles(Carta carta){
        if(color == carta.getColor() || simbolo==carta.getSimbolo()){
            return true;
        }return false; 
    }
    
    public boolean esCartaEspecial(){
    if(tipo==0){
        return true; 
    }
    return false;
    }
 
}
