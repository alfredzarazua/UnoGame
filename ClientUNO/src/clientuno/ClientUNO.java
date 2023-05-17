package clientuno;


import clientuno.modelo.Partida;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientUNO extends Application{         
    Partida current;    
    
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage){        
        try{                   
            current = new Partida();  
            current.startUNOMatch();
                        
        }catch(Exception e){
            e.printStackTrace();
        }        
    }
    
}


/*
Mas documentacion por aquí:
El primer intento de control del flujo entre una ventana fue 
usando semaforos los cuales permiten bloquear un hilo (pausar su ejecucion)
mientras otro hilo hace una tarea. En este caso se pausa el UI Thread (el que 
nos esta mostrando la ventana) y el hilo de comunicacion al servidor hace el
proceso de login en segundo plano. Esto resultó en que al "pausar" el UI Thread
la aplicacion se congelaba si las credenciales eran incorrectas.
Al estar congelada la ventana no se puede mostrar ninguna animacion de "cargando"
o una barra de progreso

La version implementada es la siguiente:
Para actualizar la interfaz se usan Tareas(Tasks) que permiten ejecutar metodos en
segundo plano. 
Para ejecutar Tareas se definió una clase UpdateUI 
Estas tareas se deben ejecutar desde el Listener Thread, el cual
no es el hilo que maneja la interfaz, entonces se usa Platform.runLater()
Esta interfaz ya está definida en la clase UpdateUI y basicamente lo que hace
es pasarle la tarea al UI Thread o hilo de la interfaz para que la ejecute.

*/