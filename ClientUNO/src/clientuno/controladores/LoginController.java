
package clientuno.controladores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ClientServer.Message;
import clientuno.modelo.StageData;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class LoginController {
    
    @FXML
    private TextField TextFieldNombre;  
    @FXML
    private TextField TextFieldContraseña; 
    @FXML
    public Label errorLabel;                     //Mensaje de error
    @FXML
    public ProgressIndicator spinner;            //Animacion de "procesando datos"
        
    
    public Stage stage; 
    public Scene scene; 
    public Parent root;  
    
    
    public void login(ActionEvent event) throws IOException, InterruptedException{
        spinner.setVisible(true);
        errorLabel.setVisible(false);
        List<String> params = new ArrayList<>();
        params.add(TextFieldNombre.getText());
        params.add(TextFieldContraseña.getText());
        //Obtenemos el socket de conexion
        Node node = (Node) event.getSource();
        stage = (Stage) node.getScene().getWindow();
        StageData data = (StageData) stage.getUserData();
        Message ds = new Message("B", -1, params); //-1 para ids desconocidos
        data.connection.sendMessage(ds);
        data.setUsername(TextFieldNombre.getText());
        stage.setUserData(data);         
    }
    
    public void loadHome() throws IOException{
        
        StageData data = (StageData) stage.getUserData();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../vistas/unoHome.fxml"));
        root = loader.load();              
        scene = new Scene(root); 
        stage.setScene(scene);                        
        
        stage.setTitle("Inicio");
        HomeController controller = loader.getController();  
        controller.setUsername(data.username);
        //traer datos antes
        controller.setRoomProperties(data.params.get(1), data.params.get(2), data.params.get(3));
        controller.setInitState();
        data.partida.setController(controller);//Actualizamos el controlador de partida
        stage.show();
     }
     
     public void loadRegisterForm(ActionEvent event) throws IOException{ 
        
        Node node = (Node) event.getSource();        
        stage = (Stage) node.getScene().getWindow();
        StageData data = (StageData) stage.getUserData();        
        stage.close();  
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../vistas/unoRegister.fxml"));
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../vistas/unoGame.fxml"));
        
        root = loader.load(); 
        stage.setUserData(data);
                   
        scene = new Scene(root); 
        stage.setScene(scene);
        stage.setTitle("Registrarse");
        RegisterController controller = loader.getController();        
        controller.setInitState();
        data.partida.setController(controller);                    //Actualizamos controlador de partida
        stage.show();        
    }
     
    public void setErrorMessage(String msg){
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        spinner.setVisible(false);        
    }
    
    public void setInitState(){
        errorLabel.setVisible(false);
        spinner.setVisible(false);
    }
}
