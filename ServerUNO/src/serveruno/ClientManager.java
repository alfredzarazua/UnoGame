
package serveruno;

import ClientServer.Message;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


/*
Clase para administrar todos los clientes

Funciones principales
*Mantener el control de todos los usuarios
*Manejar conexion/desconexion de los clientes
*Administrar los hilos de los clientes
*Administrar salas
*Crear salas y añadir usuarios

*NOTA: La eliminacion de un cliente requiere de sincronizacion en los metodos
 porque 2 hilos intentarán al mismo tiempo iterar y modificar la misma lista
 lo que genera errores de concurrencia, se usa un objeto para monitorear y bloquear
 el acceso a la lista si otro hilo la esta usando.
 los bloques de codigo con synchronized{} son los que tienen acceso concurrente
 a la lista y deben estar sincronizados.


*/
public class ClientManager {    
    private final List<GameRoom> serverRooms;
    private final List<Thread> threadsList;
    private final List<ClientThread> clientsList;
    private final Object lock;                                                  //Monitor de acceso a las listas
    
    
    public ClientManager(){        
        serverRooms = new ArrayList<>();
        threadsList = new ArrayList<>();
        clientsList = new ArrayList<>();
        lock = new Object();
    }
    
    public void createRoom(String name, String id){
        serverRooms.add(new GameRoom(name, id));
    }
    
    public void addClient(ClientThread client, Thread t){        
        threadsList.add(t);
        clientsList.add(client);
    }
    
    //NOTA: para la ejecucion local de la aplicacion las diferentes instancias de la aplicacion
    //usan la ip localhost, por lo tanto se maneja como identificador el ThreadID
    //***Para deployment de la aplicacion es mas recomendable usar IP como identificador en lugar de ThreadID
    //id del hilo listener, ip del cliente
    public void removeClient(long id, InetAddress ip, String idJoinedRoom){     //Este metodo se ejecuta cuando ocurre           
                                                                                //una excepcion en el Hilo de un cliente
        ClientThread tmpClient = null;                                          //cuando socket se cierrra es por 2 situaciones     
        for (ClientThread obj : clientsList) {                                  //La primera es que el cliente cerró la aplicacion
            if(obj.getClientId() == id){                    //la segunda es que el cliente perdió la conexion a internet
                tmpClient = obj;                                                
                break;
            }
        }
        if(tmpClient != null){
            GameRoom tmpRoom = getMyCurrentRoom(idJoinedRoom);
            if(tmpRoom != null){                
                List<String> response = new ArrayList<>();
                response.add("ok");                                  
                
                tmpRoom.removeUserFromRoom(tmpClient); 
                String total = String.valueOf(tmpRoom.getActiveUsers());
                
                response.add(total);
                response.add(tmpRoom.getRoomId());
                
                Message msg = new Message("D", -1, response);
                synchronized (lock) {                    
                    sendMessageToAllClients(msg); //Enviamos actualizacion de que se desconectó un jugador                    
                }
            }
            synchronized (lock) {
                clientsList.remove(tmpClient);
            }
            
        }  
        synchronized (lock) {
            Thread tmpThread = null;
            for (Thread cClient : threadsList) {
                if(cClient.threadId() == id){
                    tmpThread = cClient;
                    break;                
                }                
            }
            if(tmpThread != null){            
                    threadsList.remove(tmpThread); //Quitamos el hilo de la lista de hilos
                    tmpThread.interrupt();         //Liberamos los recursos deteniendo la ejecucion del hilo               
            }
        }
                        
        System.out.println("\tListener Thread killed: id = "+id);
    }
    
    public Boolean addUserToRoom(ClientThread user, String roomId){
        System.out.println("\tAdding user "+user.getSocket().getInetAddress() + " to room: "+roomId);
        GameRoom destinyRoom = null;
        for (GameRoom g : serverRooms) {
            if(g.getRoomId().equals(roomId)){
                destinyRoom = g;
            }
        }
        return destinyRoom != null && destinyRoom.joinUser(user);//si hay espacio y existe el id, lo agrega a la sala        
    }
    
    
    /*
    Envia mensaje a todos los clientes excepto al que tenga el id que se pasa como 
    parametro    
    *NOTA: Por ahora el id se toma como el identificador del hilo(Para facilitar pruebas)
     lo mejor es usar la ip del cliente
    */
    public void sendMessageToAllClients(Message msg){        
        for (ClientThread client : clientsList) {                                          
            try {                                   
                client.sendMessage(msg);
            } catch (IOException ex) {
                 
            }
        }
    }
    
    //Pone en una lista todas las salas con sus atributos(Por el momento solo es una)
    //Esta lista se usa para mostrar en la ventana Home las salas disponibles
    //La lista se vería así:
    //[Sala1 0/4 AAAA Sala2 2/4 BBBB Sala3 4/4 CCCC] por mencionar un ejemplo.
    public List<String> getAllRoomsToString(){
        List<String> values = new ArrayList<>();
        for (GameRoom i : serverRooms) {
            values.add(i.getroomName());
            values.add(String.valueOf(i.getActiveUsers())+"/4");
            values.add(i.getRoomId());                           
        }
        return values;
    }
    
    
    public GameRoom getMyCurrentRoom(String roomId){
        for (GameRoom room : serverRooms) {
            if(room.getRoomId().equals(roomId)){
                return room;
            }
        }
        return null;
    }
        
}
