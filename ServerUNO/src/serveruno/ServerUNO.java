package serveruno;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerUNO {

    public static void main(String[] args) throws IOException{
        
        
        int portNumber = 3000;                 
        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("Server is running...");
        
        
        //Escuchar conexiones de clientes, cuando se conecte uno se asigna un hilo
        ClientManager manager = new ClientManager();
        manager.createRoom("Room 1","434A3M"); //Unica sala por el momento (max 4 usuarios/sala)
        
        //Revisar si se puede pasar a un hilo cada sala, luego ver como llevar el control
        //de los hilos de las salas para que no sea una carga pesada
        //La lista o control de hilos solo es para eliminarlo una vez iniciada una partida
        //NOTA: Esta implementacion es opcional, por el momento no hay problema de procesamiento
        // si el servidor maneja 1 sala unicamente y al mismo tiempo escucha nuevas conexiones.
        
        
        
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler nc = new ClientHandler(manager, clientSocket);
            Thread clientThread = new Thread(nc);
            clientThread.start();            
            nc.setClientId(clientThread.threadId());            
            manager.addClient(nc, clientThread);      //Guardamos en la lista global                                                
        }        
    }
    
}