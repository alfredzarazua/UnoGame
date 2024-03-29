package serveruno;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ClientServer.Message;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/*
Clase para atender las solicitudes del cliente, 


Esta clase es el equivalente a Usuario, los metodos de la clase Usuario 
definida para la logica del juego se pueden importar aqui
dado que es la clase que se usa para comunicarse de manera individual con el servidor

*/

public class ClientThread implements Runnable{                            
    private Socket socket;
    private long clientId;
    private String idRoomJoined;
    ObjectOutputStream out;
    ClientManager manager; //solo es para acceso a metodos de la clase
    List<Integer> cartas = new ArrayList<>(); //cartas de cada u 
    private String userName; 
    
    
    //agrega carta al u
    public void setCarta(int num){
        cartas.add(num);   
    }
    
    public int getCarta(int num){
        return cartas.get(num);
    }
    
    //elimina carta de cartas de u 
    public void elimCarta(Integer num){
        cartas.remove(num);
    }
    
    public int getSizeCartas(){
        return cartas.size();
    }
    
    public void emptyCartas(){
        cartas.clear();
    }
    
    public void setUserName(String name){
        userName = name;
    }
    
    public String getUsername(){
        return userName;                
    }
    
    public ClientThread(ClientManager m, Socket socket) {
      this.socket = socket;
      manager = m;
    }

    @Override
    public void run() {
      try {                        
            
            //Empezar a escuchar al cliente
            listenToClient();
                                                   
        } catch (IOException e) {
            System.err.println("\tA client lost or finished its connection"); 
            manager.removeClient(clientId, socket.getInetAddress(), idRoomJoined);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setIdRoomJoined(String id){
        idRoomJoined = id;
    }
    public String getIdRoomJoined(){
        return idRoomJoined;
    } 
    //Metodo para enviar objetos serializados al cliente a traves del socket
    public void sendMessage(Message msg) throws IOException{                            
        out.writeObject(msg);
        out.flush();
    }
    public void setClientId(long id){
        clientId = id;
        
    }
    public long getClientId(){
        return clientId;
    }
    public Socket getSocket(){
        return socket;
    }
    
    //Este metodo escucha los mensajes del cliente
    public void listenToClient() throws IOException, ClassNotFoundException, SocketException{           
        //El hilo se queda ejecutando este ciclo, de manera que se queda "Escuchando al cliente"
        //por un tiempo indefinido
        
        ObjectInputStream inn = new ObjectInputStream(socket.getInputStream());   
        out = new ObjectOutputStream(socket.getOutputStream());
        Message msg = null;

        while((msg=(Message)inn.readObject()) != null && !Thread.currentThread().isInterrupted()){            
            if(msg != null){
                switch(msg.eventID){
                    case "A": //Registro de usuario
                        MySQL opcion = new MySQL();                     
                        try {                                                    
                            String res = opcion.InsertarDatosRegistro(msg.parameters);                            
                            List<String> response = new ArrayList<>();
                            response.add(res);
                            response.addAll(manager.getAllRoomsToString());
                            Message resp = new Message("A", -1, response);
                            sendMessage(resp);
                            if(res.equals("ok")){//En el registro se hace autologin, si se completó el registro, se queda el idUser
                                setUserName(msg.parameters.get(0));
                            }
                            
                        } catch (SQLException ex) {
                            List<String> response = new ArrayList<>();
                            response.add(ex.getMessage());
                            Message resp = new Message("A", -1, response);
                            sendMessage(resp);
                            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                        }                    
                        break;

                    case "B": //Login usuario
                        MySQL opcion2 = new MySQL();                     
                        try {
                            System.out.println("\tVerifying user..."); 
                            String user = msg.parameters.get(0);
                            if(!manager.checkIfLoggedIn(user)){
                                String res = opcion2.Login(msg.parameters);                            
                                List<String> response = new ArrayList<>();
                                response.add(res);
                                response.addAll(manager.getAllRoomsToString());
                                //Añadir sala como respuesta y num de jugadores
                                Message resp = new Message("B", -1, response);
                                sendMessage(resp);
                                if(res.equals("ok")){
                                    setUserName(msg.parameters.get(0));
                                }
                            }else{
                                String res = "Ya iniciaste sesión desde otra pc";
                                List<String> response = new ArrayList<>();
                                response.add(res);
                                response.addAll(manager.getAllRoomsToString());
                                //Añadir sala como respuesta y num de jugadores
                                Message resp = new Message("B", -1, response);
                                sendMessage(resp);
                            }
                            
                            
                        } catch (SQLException ex) {
                            List<String> response = new ArrayList<>();
                            response.add(ex.getMessage());
                            Message resp = new Message("B", -1, response);
                            sendMessage(resp);
                            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                        }                    
                        break;
                    case "C"://Añadir usuario a una sala
                        List<String> response = new ArrayList<>();
                        //La lista contiene unicamente el id de la sala que el usuario solicitó (parameters.get(0))
                        Boolean res = manager.addUserToRoom(this, msg.parameters.get(0));
                        if(res){
                            response.add("ok");                             
                            setIdRoomJoined(msg.parameters.get(0));
                            response.add(idRoomJoined);
                        }else{
                            response.add("Error");
                        }
                        Message resp = new Message("C", -1, response);
                        sendMessage(resp);
                                                
                        //inmediatamente enviamos actualizacion del total de jugadores en la sala                        
                        GameRoom currentRoom = manager.getMyCurrentRoom(idRoomJoined);
                        if(currentRoom != null){
                            int total = currentRoom.getActiveUsers();
                            response = new ArrayList<>();
                            response.add("ok");                            
                            response.add(String.valueOf(total));
                            response.add(idRoomJoined);
                            resp = new Message("D", -1, response); 
                            manager.sendMessageToAllClients(resp); 
                        }                                              
                        break;
                    case "E"://Solicitud de lista de salas y sus propiedades
                            response = new ArrayList<>();
                            response.add("ok");
                            response.addAll(manager.getAllRoomsToString());
                            //Añadir sala como respuesta y num de jugadores
                            resp = new Message("E", -1, response);
                            sendMessage(resp);
                        break;
                    case "F": //da turnos
                        break; 
                        
                    case "G": //da cartas
                        break; 
                    case "H":// dar arr de usuarios
                        break;
                    case "I":
                        //este inicializa partida y usa mensajes F,G y H y J
                            if("ok".equals(msg.parameters.get(0))){
                               GameRoom currentRoom2 = manager.getMyCurrentRoom(idRoomJoined);
                                if(currentRoom2 != null){
                                    //enviamos mensaje de actualizacion de la pantalla (pasa automaticamente de sala de espera a juego)
                                    response = new ArrayList<>();
                                    response.add("ok");                                    
                                    resp = new Message("I", -1, response);                                    
                                    //Enviar orden de cambio de ventana
                                    currentRoom2.sendMessageToRoomMembers(resp);
                                    
                                    //iniciar juego
                                    currentRoom2.inicializaPartida();  
                                } 
                            }
                       
                        break;
                    case "J":
                        //actualiza informacion: da carta y nuevo turno 
                        break;
                    case "K":
                        //manda carta
                        if("ok".equals(msg.parameters.get(0))){
                               GameRoom currentRoom2 = manager.getMyCurrentRoom(idRoomJoined);
                            if(currentRoom2 != null){
                              int ans=  currentRoom2.chequeoCarta(Integer.parseInt(msg.parameters.get(1)));
                              if( ans==-1 || ans==0){// si no es correcta, o accedemos colores
                                   response = new ArrayList<>();
                                   response.add("ok");
                                   response.add(Integer.toString(ans));
                                   resp = new Message("L", -1, response);
                                   sendMessage(resp);
                                   
                              }else if(ans == 1){
                                   currentRoom2.aumentoTurno();
                                   currentRoom2.nuevoTurno();
                               }
                            }
                            
                            
                            }
                        break;
                    case "M":
                        //manda carta nueva
                        break;
                    case "N":
                        //manda turno nuevo
                        break;  
                    case "O":
                        //Cuando un usuario pida comida, este le devolvera  carta
                        GameRoom currentRoom1 = manager.getMyCurrentRoom(idRoomJoined);
                        if(currentRoom1 != null){
                            response = new ArrayList<>();
                            response.add("ok"); 
                            response.add(currentRoom1.getComida()); 
                            resp = new Message("P", -1, response);
                            sendMessage(resp); 
                        }
                        break; 
                    case "P": // manda nueva carta
                        break; 
                    case "Q": // manda opcion de color
                        if("ok".equals(msg.parameters.get(0))){
                               GameRoom currentRoom2 = manager.getMyCurrentRoom(idRoomJoined);
                            if(currentRoom2 != null){
                               currentRoom2.cambioColorCartaEspecial(msg.parameters.get(1));   
                            } 
                            }
                        break; 
                    case "R": 
                           //manda castigo en el caso de que es mas 2
                         if("ok".equals(msg.parameters.get(0))){
                               GameRoom currentRoom2 = manager.getMyCurrentRoom(idRoomJoined);
                            if(currentRoom2 != null){
                                //response = new ArrayList<>();
                                response = currentRoom2.efectoSigUsuario();
                                resp = new Message("S", -1, response);
                                sendMessage(resp);
                                //tengo que ponerlo aqui para que agregue
                                currentRoom2.aumentoTurno();
                                currentRoom2.nuevoTurno();
                            } 
                            } 
                        
                        break;
                        
                        case"U"://Un usuario presionó el boton Salir(Solo de la partida NO de la aplicacion)
                            // si un usuario lo presiona, y queda 1 solo jugador
                            //ambos deberan salirse de la partida
                            //si son mas de 2, y se sale uno, continua el juego y actualiza toda la informacion 
                            if("ok".equals(msg.parameters.get(0))){                                   
                                GameRoom currentRoom2 = manager.getMyCurrentRoom(idRoomJoined);
                             
                                //regreso las cartas del usuario que presiono boton 
                                /*ERROR/////////////////////////////////////////
                                removeUserCardsFromGame(this) solo quita las cartas del jugador que salio 
                                por lo que se supone que deberiamos tambien llamar a
                                removeUserFromRoom(this); que esta comentado 
                                Esto porque sin hacerlo ya solo me sale un jugador si imprimo activeUsers
                                
                                */
                                if(currentRoom2 != null){
                                    currentRoom2.removeUserCardsFromGame(this);                                
                                    //eliminar de la SALA
                                    currentRoom2.removeUserFromRoom(this);
                                    //enviar actualizacion de numero de jugadores en la sala
                                    int total = currentRoom2.getActiveUsers();
                                    response = new ArrayList<>();
                                    response.add("ok");                            
                                    response.add(String.valueOf(total));
                                    response.add(idRoomJoined);
                                    resp = new Message("D", -1, response); 
                                    manager.sendMessageToAllClients(resp); 

                                    //Enviar orden de ir a Home, enviar datos de las salas disponibles
                                    response = new ArrayList<>();
                                    response.add("ok");
                                    response.addAll(manager.getAllRoomsToString());                                
                                    resp = new Message("B", -1, response);
                                    sendMessage(resp);//Solo el cliente que presiono el boton salir será enviado a home
                                    
                                    if(currentRoom2.getActiveUsers()==1){                                                                                                                                                                     
                                        /* esto notifica al jugador que se quedó solo en la sala                                   
                                        */
                                         response = new ArrayList<>();
                                         response.add("ok");
                                         resp = new Message("V", -1, response);
                                         currentRoom2.sendMessageToRoomMembers(resp);                                                                   
                                    }else if(currentRoom2.getActiveUsers()>1){                                    
                                        currentRoom2.reenvioInformacionUsuario();
                                    }
                                }
                                //Si currentRoom2 es null, se ha desconectado o ha presionado salir de la aplicacion
                                //Esto se maneja en excepciones
                                                                                                                                                                                              
                                /* TAMBIEN HAY UN PROBLEMA EN GAMEROOM 
                                en la funcion de reenvioInformacion, y es el mism problema (ve a ella a leer lo q escribi) 

                                1. si imprimo el tamaño del arreglo de los jugadores me muestra q es 0 
                                2. si descomento removeUserFromRoom(this) e imprime active user me sale 0 
                                es decir que hay un momento antes de que llegue a este mensaje que borran a almenos 1 jugador
                                Donde? no se 

                                */                             
                            }
                            break;
                            
                        case "W":
                            if("ok".equals(msg.parameters.get(0))){
                               GameRoom currentRoom2 = manager.getMyCurrentRoom(idRoomJoined);
                                if(currentRoom2 != null){
                                    //iniciar juego
                                    currentRoom2.inicializaPartida();  
                                } 
                            }
                            break;
                            
                        
                } 
            }
        }                
    }
    
    
}
