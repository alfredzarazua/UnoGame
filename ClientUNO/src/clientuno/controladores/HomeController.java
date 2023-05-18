
package clientuno.controladores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ClientServer.Message;
import clientuno.modelo.StageData;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
public class HomeController {
    public Stage stage; 
    private Scene scene; 
    private Parent root; 
    
    @FXML
    private Label username;                      //Nombre del jugador
    @FXML
    private Label roomLabel1;                    //Nombre de la sala
    @FXML
    private Label playersLabel1;                 //Jugadores conectados
    @FXML
    private Label idLabel1;                      //ID de la sala
    @FXML
    public Label errorLabel;                     //Mensaje de error
    @FXML
    public ProgressIndicator spinner;            //Animacion de "procesando datos"

    
    public void instructionsButton(ActionEvent event) throws IOException{
       
        Node node = (Node) event.getSource();        
        stage = (Stage) node.getScene().getWindow();
        StageData data = (StageData) stage.getUserData();        
        stage.close();  
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../vistas/unoInstructions.fxml"));
        root = loader.load(); 
        stage.setUserData(data);//Enviamos el socket a la ventana que se abrirá   
                      
        scene = new Scene(root); 
        stage.setScene(scene);
        stage.setTitle("Instructions");
        InstructionsController controller = loader.getController();
        controller.setInitState();
        
        stage.show();          
    }
    
    //Muestra la ventana de espera una vez que se aprobó unirse a una sala     
    public void loadWaitingView() throws IOException{
        StageData data = (StageData) stage.getUserData();   
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../vistas/unoWaitingRoom.fxml"));
        root = loader.load(); 
        if(data.connection.getSocket() == null){
            System.out.println("Error de conexión");
        }else{
            System.out.println("Conexión correcta");
        }
        data.setUsername(username.getText());
        stage.setUserData(data);
        stage.close(); 
        
        /*
        Nota: data.params se usa para actualizar el num de usuarios activos en la sala
        Usar esta lista para otra cosa puede hacer que se mezclen los datos porque funciona
        de manera asíncrona, investigar otra forma en caso de ser necesario
        */
        
        scene = new Scene(root); 
        stage.setScene(scene);
        stage.setTitle("Uno Game");        
        
        //Aqui pasar los datos de la sala y del usuario
        //
        
        
        WaitingController controller = loader.getController();
        controller.stage = stage; //correccion de error de conexion al cambiar de ventana
        data.partida.setController(controller);
        controller.botonDeshabilitaInicializarPartida();
        controller.setMainMessage(username.getText());
        controller.setRoomDataMessage(idLabel1.getText(), roomLabel1.getText());
        
        stage.show(); 
    }
    
    //Pasar el nombre del jugador a la ventana Home
    public void setUsername(String user){
        username.setText(user);
    }
    
    //Asigna nombre, id y num de jugadores
    public void setRoomProperties(String name, String players, String id){
        roomLabel1.setText(name);
        playersLabel1.setText(players);
        idLabel1.setText(id);        
    }
    
    public void updateNumPlayers(String count){
        String tmp = count + "/4";
        playersLabel1.setText(tmp);
    }
    
    //Solicitar unirse a una sala
    //Actualmente solo esta habilitada una
    public void requestToJoinRoom(ActionEvent event) throws IOException{
        
        errorLabel.setVisible(false);
        spinner.setVisible(true);
        List<String> params = new ArrayList<>();        
        params.add(idLabel1.getText());            //solo se envia el id de la sala
        
        Node node = (Node) event.getSource();
        stage = (Stage) node.getScene().getWindow();
        StageData data = (StageData) stage.getUserData();
        Message ds = new Message("C", -1, params); //-1 para ids desconocidos
        data.connection.sendMessage(ds);                
    }
    
    //Muestra un mensaje de error en la ventana
    public void setErrorMessage(String msg){
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        spinner.setVisible(false);        
    }
    
    //Al abrir la ventana se oculta el spinner y la etiqueta de error
    public void setInitState(){
        errorLabel.setVisible(false);
        spinner.setVisible(false);
    }
    
}
