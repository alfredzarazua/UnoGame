
package clientuno.modelo;

import ClientServer.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;



//clase del Listenner Thread
public class ServerListener implements Runnable{
    private final Socket socket;
    private final Partida partida;
    
    public ServerListener(Socket socket, Partida current){
        this.socket = socket;
        this.partida = current;
    }
    
    @Override
    public void run(){
        try {            
            listenToServer();
        } catch (IOException ex) {
            System.err.println("Listener stopped");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch(InterruptedException ex){
            System.err.println("Listener has ended");
        }
    }
    
    //El hilo se queda ejecutando este ciclo, de manera que se queda "Escuchando al servidor"
        //por un tiempo indefinido  
    
    //NOTA: Los hilos de actualizar interfaz reciben como parametro una Tarea (Task)
    //e inician su ejecucion con .start()
    public void listenToServer() throws IOException, ClassNotFoundException, InterruptedException{              
                   
        ObjectInputStream inn = new ObjectInputStream(socket.getInputStream());        
        Message msg = null;
        
        while((msg=(Message)inn.readObject()) != null && !Thread.currentThread().isInterrupted()){ 
            if(msg != null){
                switch(msg.eventID){
                    case "A"://Confirmacion de registro
                        if(msg.parameters.get(0).equals("ok")){                                                       
                            partida.clearStageParamsList();
                            partida.setStageParamsList(msg.parameters);         //Se recibe la lista de salas disponibles
                            new Thread(new UpdateUI(partida, 1, "")).start();   //Actualizar Interfaz
                        }                                                    
                        else{
                            final String res = msg.parameters.get(0);                            
                            new Thread(new UpdateUI(partida, 2, res)).start();                           
                        }                            
                        break;
                    case "B"://Confirmacion de inicio de sesion                        
                        if(msg.parameters.get(0).equals("ok")){                                                                                                                  
                            partida.clearStageParamsList();
                            partida.setStageParamsList(msg.parameters);         //se recibe una lista de salas disponibles
                            new Thread(new UpdateUI(partida, 3)).start();       //Actualizar Interfaz                                                     
                        }                            
                        else{                                                       
                            final String res = msg.parameters.get(0);                            
                            new Thread(new UpdateUI(partida, 4, res)).start();  //Actualizar Interfaz                            
                        }                        
                        break;
                    case "C"://Respuesta de solicitud de unirse a una sala
                        if(msg.parameters.get(0).equals("ok")){
                            System.out.println("Request to join acepted");  
                            partida.s_data.setIdRoomJoined(msg.parameters.get(1));
                            new Thread(new UpdateUI(partida, 5)).start();       //Actualizar Interfaz                                                        
                        }
                        else{
                            final String res = msg.parameters.get(0);                            
                            new Thread(new UpdateUI(partida, 6, res)).start();  //Actualizar Interfaz 
                        }
                        break;
                    case "D"://Usuario entró/salió de la sala, se debe actualizar el estado
                        if(msg.parameters.get(0).equals("ok")){                            
                            System.out.println("Updated active users count");                                                                                                                                               
                            partida.clearStageParamsList();
                            partida.setStageParamsList(msg.parameters);
                            new Thread(new UpdateUI(partida, 7)).start();       //Actualizar Interfaz                                                                                                                
                        }                        
                        break;
                    case "E"://Respuesta de solicitud de lista de salas
                        if(msg.parameters.get(0).equals("ok")){                                                                                                                  
                            partida.clearStageParamsList();
                            partida.setStageParamsList(msg.parameters);         //se recibe una lista de salas disponibles
                            new Thread(new UpdateUI(partida, 3)).start();       //Actualizar Interfaz                                                                                 
                        }                                                    
                        break; 
                    case "F":
                        //recibe turno de usuario(el propio) no el actual
                        if(msg.parameters.get(0).equals("ok")){
                            partida.setPartida(Integer.parseInt(msg.parameters.get(1)));
                        }
                        break;
                    case "G":
                        //manda cartas
                        if(msg.parameters.get(0).equals("ok")){
                            partida.igualarJuegoCartas(msg.parameters);
                            //Cargar cartas
                            new Thread(new UpdateUI(partida, 10)).start(); 
                        }
                        break;
                    case "H"://                                                 Lista de usuarios 
                        //
                        if(msg.parameters.get(0).equals("ok")){                            
                            partida.setUsernames(msg.parameters);
                            
                        }
                        break;
                        
                    case "I"://notificacion del servidor para iniciar el juego(alguien en tu sala pulso el boton iniciar juego)
                        //Saltar de sala de espera a juego checar donde se envia
                        if(msg.parameters.get(0).equals("ok")){
                            new Thread(new UpdateUI(partida, 8)).start();   //Actualizar Interfaz                               
                        }                        
                        break;
                        
                    case "J":                                                   //carta mesa/turno actual/bandera
                        //actualiza carta de la mesa y turno                         
                        if(msg.parameters.get(0).equals("ok")){
                           partida.setCartaMesa(Integer.parseInt(msg.parameters.get(1)));
                           partida.nuevoTurnoActual(Integer.parseInt(msg.parameters.get(2)),Boolean.parseBoolean(msg.parameters.get(3)));
                           new Thread(new UpdateUI(partida, 9)).start();//turno actual
                           new Thread(new UpdateUI(partida, 11)).start();//carta de la mesa
                           
                        }
                        break;
                    case "K": // funvion manda al servidor 
                        break; 
                    case "L": // manda respuesta de la carta 
                        //int c=12;
                        //int var;
                        if(msg.parameters.get(0).equals("ok")){
                            if(msg.parameters.get(1).equals("0")){
                                 new Thread(new UpdateUI(partida, 14)).start();
                            }else if(msg.parameters.get(1).equals("-1")){
                               new Thread(new UpdateUI(partida, 12)).start();
                            }
                            /* se deben de mostrar naamas
                                0= no es carta correcta
                                1= es carta correcta// este caso no va a entrar 
                                -1 = activa boton
                                */ 
                        }
                        break; 
                    case "M":
                        //nueva carta
                        if(msg.parameters.get(0).equals("ok")){
                            partida.setCartaMesa(Integer.parseInt(msg.parameters.get(1)));
                            new Thread(new UpdateUI(partida, 11)).start();
                        }
                        break;
                    case "N":
                        //nueva turno
                        if(msg.parameters.get(0).equals("ok")){
                             partida.nuevoTurnoActual(Integer.parseInt(msg.parameters.get(1)),Boolean.parseBoolean(msg.parameters.get(2)));
                            new Thread(new UpdateUI(partida, 9)).start(); 
                        }
                        break;
                    case "P": 
                        if(msg.parameters.get(0).equals("ok")){
                            partida.setJuegoCarta(Integer.parseInt((msg.parameters.get(1))));
                            new Thread(new UpdateUI(partida, 10)).start();
                        }
                        break;
                    case "S":
                        if(msg.parameters.get(0).equals("ok")){
                            partida.setJuegoCartas(msg.parameters);
                            new Thread(new UpdateUI(partida, 10)).start();//actualizar cartas del jugador
                        }
                        break;
                    case "T": //Termina juego
                        new Thread(new UpdateUI(partida, 13)).start();
                        break;
                        
                    case "U": //no se usa
                        if(msg.parameters.get(0).equals("ok")){                                                                                                                  
                            partida.clearStageParamsList();
                            partida.setStageParamsList(msg.parameters);         
                            new Thread(new UpdateUI(partida, 15)).start();      //Actualizar Interfaz                                                     
                        } 
                       break; 
                       
                    case "V"://Solo hay un usuario en el juego, automaticamente ir a home
                        if(msg.parameters.get(0).equals("ok")){ 
                            
                            new Thread(new UpdateUI(partida, 16)).start();       //Mostrar notificacion de salida de la partida                                                                                 
                        } 
                        break;
                    
                    
                }
            }
        } 
    }        

}
