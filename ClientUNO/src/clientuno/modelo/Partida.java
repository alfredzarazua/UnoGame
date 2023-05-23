/*    
        
        *Esta clase controla toda la aplicacion 
        *Mantiene los datos de usuario y conexion
        *Tiene metodos para actualizar la interfaz(Estan referenciados en UpdateUI)
    
 */
package clientuno.modelo;

import ClientServer.Message;
import clientuno.ClientUNO;
import clientuno.controladores.GameController;
import clientuno.controladores.HomeController;
import clientuno.controladores.RegisterController;
import clientuno.controladores.LoginController;
import clientuno.controladores.WaitingController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Partida extends ClientUNO{        
    ServerConnection sc;  
    Parent root;
    Scene scene;
    Stage stage;
    FXMLLoader loader;
    StageData s_data; 
    
    int turno; 
    public List<Integer> cartas;
    List<String> usernames;
    int turnoActual;
    int cartaMesa; 
    boolean castigo; 
    

    public Partida(){
        stage = new Stage();        
    }
    
    //inicio de la aplicacion, Login
    public void startUNOMatch(){        
        try{                        
            
            sc = new ServerConnection(this);// Inicia automaticamente la conexion al servidor
            loader = new FXMLLoader(getClass().getResource("../vistas/unoLogin.fxml"));
            root = loader.load();            
            s_data = new StageData(this, "");
            stage.setUserData(s_data);
            scene = new Scene(root);                           
            stage.setScene(scene); 
            stage.setTitle("Login");  
            LoginController controller = loader.getController();              
            controller.setInitState();                                    
            stage.show();  
            stage.setOnCloseRequest(event -> {
                event.consume();
                closeApp(stage);
            });
                                                
                        
        }catch(Exception e){
            e.printStackTrace();
        }        
    }
    
    //Saltar a la ventana de inicio, muestra nombre del jugador
    public void loginSuccessful(){
        LoginController controller = loader.getController();
        try{
            controller.loadHome();
        }catch(IOException e){
            e.printStackTrace();
        }                        
    }
    
    public void exitPlayer() throws IOException{
        /*
    }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notificación");
        alert.setHeaderText("No hay mas jugadores disponibles");
        alert.setContentText("Deseas abandonar la partida?");
        
        if(alert.showAndWait().get()==ButtonType.OK){*/
        List <String> resp = new ArrayList<>();
        resp.add("ok");
        Message message= new Message("U", -1,resp);  
        StageData data = (StageData) stage.getUserData();
        data.connection.sendMessage(message);
       // }
        
    }
    
    public void loadH(){
        GameController controller = loader.getController();
        try{
            controller.loadHome();
        }catch(IOException e){
            e.printStackTrace();
        }                        
    }
    
    //Ocultar spinner y mostrar mensaje de error
    public void loginFailed(String msg){
        LoginController controller = loader.getController();
        controller.setErrorMessage(msg);
    }
        
     //Saltar a la ventana de inicio, muestra nombre del jugador
    public void registerSuccessful(){
        //Actualizar controlador a login (login tiene el metodo loadHome())
        LoginController controller = new LoginController();
        controller.stage = stage;
        setController(controller);        
        try{
            controller.loadHome();
        }catch(IOException e){
            e.printStackTrace();
        }                        
    }
    
    //Ocultar spinner y mostrar mensaje de error
    public void registerFailed(String msg){
        RegisterController controller = loader.getController();
        controller.setErrorMessage(msg);
    }
    
    //Una vez que se unió a la sala, debe esperar a que se unan otros jugadores
    //Aqui se envia a la ventana de espera
    public void joinedToRoom(){
        HomeController controller = loader.getController();                
        try {
            controller.loadWaitingView();              
        } catch (IOException ex) {
            Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Notificar que no se pudo unir a la sala
    public void rejectedJoinToRoom(String msg){
        HomeController controller = loader.getController();                
        controller.setErrorMessage(msg);
    }
    
    //Actualizar numero de jugadores conectados [Vista sala de espera] 
    //*NOTA: s_data.params contiene datos que envió el servidor
            //estos datos se asignaron previamente en el switch case de la clase ServerListener
    public void updateActiveUsersInRoom(){        
        String count = s_data.params.get(1);    
        Object obj = loader.getController();        
        //si estaba en la sala donde entró/salió alguien (Esta en pantalla de espera)
        if(obj.getClass().equals(WaitingController.class) && s_data.params.get(2).equals(s_data.idRoomJoined)){            
            WaitingController controller = loader.getController();                
            controller.setActiveUsersLabel(count);
        }
        else if(obj.getClass().equals(HomeController.class)){            
            HomeController controller = loader.getController(); 
            controller.updateNumPlayers(count);
        }        
    }
    //Saltar a la ventana de juego
    public void openGameView(){        
        WaitingController controller = loader.getController();
        try{
            controller.loadGameView();
        }catch(IOException e){
            e.printStackTrace();
        }                        
    }
    
    //Metodo para cambiar el controlador de la partida
    //Dependiendo a que ventana se cambiará, el controlador de la partida debe
    //ser el mismo, antes de abrir una nueva ventana, actualizar el controlador
    //(Es necesario hacerlo si partida ejecutará metodos del controlador)
    public void setController(Object controller){
        loader.setController(controller);
    }
    
    //Agrega la lista de parámetros recibidos en un mensaje
    //la lista pertenece a partida y facilita el acceso en cualquier parte de la aplicacion
    public void setStageParamsList(List<String> e){
        s_data.setParamsList(e);
        stage.setUserData(s_data);
    }
    
    //Borra los elementos de la lista de parametros recibidos del servidor
    //usar siempre antes de agregar nuevos parámetros
    public void clearStageParamsList(){
        s_data.clearParamsList();
    }
    
    //Cierra la aplicacion, detiene el hilo Listener
    public void closeApp(Stage stage){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close UNO");
        alert.setHeaderText("You´re about to exit");
        alert.setContentText("Do you want to exit?");
    
        if(alert.showAndWait().get()==ButtonType.OK){                                    
            try {
                sc.stopListening();
            } catch (IOException ex) {
                
            }
            stage.close();
            System.err.println("UNO app succesfully closed");
        }
    }
    
    public void setPartida(int turno){
        this.turno= turno; 
    }
    
    //Revisar es necesario separar? recibe la lista de cartas, se tendría que pasar en UPDATEUI?
    public void setJuegoCartas(List<String> car){
        
        for(int i=1; i<car.size(); i++){
            cartas.add(Integer.valueOf(car.get(i)));
        }

        /*GameController controller = loader.getController();  //Genera Excepcion not the UI thread
        controller.renderUNOCards(cartas);   //fallo */    
    }
    
    public void igualarJuegoCartas(List<String> car){
       cartas= new ArrayList<>();
       for(int i=1; i<car.size(); i++){
           cartas.add(Integer.valueOf(car.get(i)));
       }
       /*
        GameController controller = loader.getController();
        controller.renderUNOCards(cartas);*/
    }
    
    public void setUsernames(List<String> u){        
        usernames= new ArrayList<>();
        for(int i=1; i<u.size(); i++){
            usernames.add(u.get(i));
        }
    }
    
    public List<String> getUsernames(){
        return usernames;
    }
    
    public void setCartaMesa(int cartaMesa){
        this.cartaMesa=cartaMesa;        
    }
    
    public void nuevoTurnoActual(int turnoActual, boolean castigo) throws IOException{
        this.turnoActual=turnoActual;
        this.castigo= castigo;       
        actualizaInformacion();
    }
    
    /////////77falta checar es algo asi 
    public void actualizaInformacion() throws IOException{
        if(turno==turnoActual){
           if(castigo){
                List<String> msg = new ArrayList<>();
                msg.add("ok");
                Message message= new Message("R", -1,msg);
                StageData data = (StageData) stage.getUserData();
                data.connection.sendMessage(message);
            //manda mensaje de que le toca castigo
        }
        }
        
    }
    
    public void setJuegoCarta(int carta){
        cartas.add(carta);
    }
    
    public void updateCartaMesa(){
                
        GameController controller = loader.getController();                        
        controller.renderCartaActual(cartaMesa);                                  
    }
    
    //caso j  actualizar turno de juego
    public void setTurnData(){
        System.out.println("Turno: "+turnoActual);
        
        GameController controller = loader.getController();
        controller.inicializarPartida();
        controller.renderTurnoActual(usernames.get(turnoActual)); 
        if( turno== turnoActual){
            controller.habilitaTurno(); 
        }

        
    }
    public void showText(){
        GameController controller = loader.getController();                                                       
        controller.visibleNombreGanador();  
        
    }
    
     public void setWinner(){
        GameController controller = loader.getController();  
        
        controller.inicializarPartida();
        controller.setWinner(usernames.get(turnoActual));
       
        
     }
     
    public void showColorButtoms(){
        GameController controller = loader.getController(); 
        controller.inicializarPartida();
        controller.habilitaColorPane();  
    }
    
    //Actualizar todas las cartas del usuario
    public void updateCards(){
        GameController controller = loader.getController();                        
        controller.renderUNOCards(cartas);
    }
    
    
    

  
}
