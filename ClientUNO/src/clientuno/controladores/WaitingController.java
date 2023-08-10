
package clientuno.controladores;

import ClientServer.Message;
import clientuno.modelo.StageData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;



public class WaitingController {    
    public Stage stage; 
    public Scene scene; 
    public Parent root; 
    
    @FXML
    private Label labelActiveUsers;
    
    @FXML
    private Button botonIniciarPartida;
    
    @FXML
    private Label mainMessage;
    
    @FXML
    private Label roomData;
    

    
    //checar si StageData devuelve el username, sino agregarlo en home controller
        
    //Actualiza el contador de usuarios activos en la sala
    public void setActiveUsersLabel(String count){
        String tmp = count + "/4";
        labelActiveUsers.setText(tmp);
        if( Integer.parseInt(count)>1){
            botonIniciarPartida.setDisable(false);  
        }
    }
    
    public void setMainMessage(String userName){
        mainMessage.setText("Hey "+userName+", Espera a que otros jugadores se unan a tu sala...");
    }
    
    public void setRoomDataMessage(String idRoom, String nameRoom){
       /* this.idRoom= idRoom;
        this.nameRoom= nameRoom;*/
        roomData.setText(nameRoom + " - " + idRoom);
    }
    
    public void loadGameView() throws IOException{ //cambia pantalla
        StageData data = (StageData) stage.getUserData();   //null
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../vistas/unoGame.fxml"));
        root = loader.load();                             
        scene = new Scene(root); 
        stage.setScene(scene);
        stage.setTitle("Uno Game");
        stage.setUserData(data);//Pasar Socket a la siguiente ventana
        
        
        GameController controller = loader.getController();
        data.partida.setController(controller);
        controller.stage = stage; 
        controller.renderUNOCards(data.partida.cartas);
        controller.inicializarPartida();
        controller.showUsernames(data.partida.getUsernames());
        controller.setRoomName(roomData.getText());        
        stage.show(); 
        
    }
    
    
    
    public void botonDeshabilitaInicializarPartida(){
        botonIniciarPartida.setDisable(true);  
    }
    
    
    public void solicitarInicioPartida(ActionEvent event) throws Exception{
        Node node = (Node) event.getSource();        
        stage = (Stage) node.getScene().getWindow();
        StageData data = (StageData) stage.getUserData();
        
        List<String> msg = new ArrayList<>();
        msg.add("ok");
        Message message= new Message("I", -1,msg);
        //loadGameView(); //no se debe mandar llamar aqui, este metodo va en la respuesta que envie el servidor(la respuesta es a todos los miembros de la sala)        
        data.connection.sendMessage(message);   
        stage.setUserData(data);
    }
    
    
    
}
