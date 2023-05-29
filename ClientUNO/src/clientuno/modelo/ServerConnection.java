package clientuno.modelo;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import ClientServer.Message;

  

public final class ServerConnection {     
    private Socket socket;
    private Thread listener;
    private ObjectOutputStream out;
    
    public ServerConnection(Partida partida){
        try {            
            connectToServer(partida);
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Socket getSocket(){
        return socket;
    }
    
    public Thread getListener(){
        return listener;
    }
            
    //Documentacion
    //Error al intentar hacer login mas de 1 vez (Corregido)
    //https://stackoverflow.com/questions/2393179/streamcorruptedexception-invalid-type-code-ac
    //Serializa el objeto y lo envia como cadena
    public void sendMessage(Message msg) throws IOException{                              
        out.writeObject(msg);
        out.flush();
    }
    
    
    //Iniciar conexion al servidor
    public void connectToServer(Partida current) throws IOException{        
        String hostname = "192.168.1.165";  
        int portNumber = 3000;                              //puerto
        socket = new Socket(hostname, portNumber);  
                
        listener = new Thread(new ServerListener(socket,current));
        
        listener.start();//empezar a escuchar al servidor(Esperar a que se unan jugadores)              
        
    }
    
    //Cierra la conexion con el servidor y termina el hilo
    public void stopListening() throws IOException{                                
        socket.close();
        listener.interrupt();        
    }      
}

