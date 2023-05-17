
package clientuno.modelo;

import javafx.application.Platform;
import javafx.concurrent.Task;



//Esta clase ejecuta tareas (Tasks) en el UI Thread, la tarea se hace en segundo
//plano y puede ayudar a actualizar la interfaz desde un hilo que no es el UI Thread
//NOTA: Las tareas no se ejecutan por si solas, se deben asignar a un nuevo Hilo
//El hilo tambien debe iniciarse con .start()
//

/*
¿Por que es necesario hacerlo con Platform.runLater() ?

In Java, the Platform.runLater() method is used to execute a Runnable on the ***JavaFX 
Application Thread***. The JavaFX Application Thread is responsible for rendering the 
user interface of a JavaFX application, and it is the only thread that SHOULD 
modify the user interface.

If you need to update the user interface of a JavaFX application from a non-JavaFX 
thread (such as a background thread), you can use the Platform.runLater() method 
to execute a Runnable on the JavaFX Application Thread. This ensures that the 
user interface is only modified on the correct thread, preventing potential 
concurrency issues and ensuring that the user interface remains responsive.


En resumen, el hilo Listener del cliente no puede actualizar la interfaz grafica
porque no es el UI Thread, por lo tanto no tiene permiso ni acceso de hacerlo
Entonces esta clase solo prepara la tarea y se la pasa al UI Thread para que el se
haga cargo.
*/
public class UpdateUI extends Task<Void>{
    
    private final String msg;
    private final Partida partida;
    private final int runId;    
    
    public UpdateUI(Partida m,int id, String msg){
        this.partida = m;
        runId = id;
        this.msg = msg;    
    }
    public UpdateUI(Partida m, int id){
        msg = "";
        this.partida = m;
        runId = id;        
        
    }
    
    //La tarea ejecutará solo el metodo que corresponda al id que se le asigne en
    //el constructor, de ser necesario se pueden añadir atributos y constructores a
    //esta clase
    @Override protected Void call() throws Exception {                                    
        Platform.runLater(() -> {  //Funcion flecha  (indica que usa la interfaz Runnable)
            switch(runId){
                case 1 -> partida.registerSuccessful();          //Auto login, ir a Home
                case 2 -> partida.registerFailed(msg);           //Mostrar mensaje de error
                case 3 -> partida.loginSuccessful();             //ir a Home
                case 4 -> partida.loginFailed(msg);              //Mostrar mensaje de error
                case 5 -> partida.joinedToRoom();                //ir a sala de espera
                case 6 -> partida.rejectedJoinToRoom(msg);       //Mostrar mensaje de error
                case 7 -> partida.updateActiveUsersInRoom();     //Actualizar numero de usuarios
                case 8 -> partida.openGameView();                //ir a ventana de juego
            }
        });                               
        return null;                        
    }    
}
