/*
    Clase para almacenar los datos de la escena antes de que se destruya
    Almacena el socket (Conexion al servidor)    
 */
package clientuno.modelo;

import java.util.ArrayList;
import java.util.List;

public class StageData {    
    public ServerConnection connection;
    public String username;
    public Partida partida;
    public String idRoomJoined;
    public List<String> params;                    //lista temporal para almacenar datos recibidos del servidor

    public StageData(Partida partida, String user) {   
        this.partida = partida;
        connection = partida.sc;
        username = user; 
        params = new ArrayList<>();
    } 
    public void setIdRoomJoined(String id){
        idRoomJoined = id;
    }
    public void setUsername(String user){
        username = user;
    }
    public void setParamsList(List<String> e){
        params.addAll(e);        
    }
    public void clearParamsList(){
        params.clear();
    }
}
