
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
import clientuno.ClientUNO;
import clientuno.modelo.StageData;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class RegisterController extends ClientUNO{ 
    
    @FXML
    TextField TextFieldUsername;
    
    @FXML
    TextField TextFieldNombre; 
    
    @FXML
    TextField TextFieldContraseña;
           
    @FXML
    TextField TextFieldContraseña2;
    
    @FXML
    public Label errorLabel;                     //Mensaje de error
    
    @FXML
    public ProgressIndicator spinner;            //Animacion de "procesando datos"
    
    private Stage stage; 
    private Scene scene; 
    private Parent root;     
        
    
    public void register(ActionEvent event) throws IOException{ 
        if(TextFieldContraseña.getText().equals("") | TextFieldNombre.getText().equals("") | TextFieldUsername.getText().equals("") ){
            
            setErrorMessage("All fields required!");
            
        }else if(TextFieldContraseña.getText().equals(TextFieldContraseña2.getText())){
            
            spinner.setVisible(true);
            errorLabel.setVisible(false);
            
            List<String> params = new ArrayList<>();
            params.add(TextFieldUsername.getText());
            params.add(TextFieldNombre.getText());
            params.add(TextFieldContraseña.getText());
            
            Node node = (Node) event.getSource();            
            stage = (Stage) node.getScene().getWindow();
            StageData data = (StageData) stage.getUserData();                  //Obtenemos el socket de conexion
            Message ds = new Message("A", -1, params);                         
            
            data.connection.sendMessage(ds);
            data.setUsername(TextFieldUsername.getText());
            stage.setUserData(data);
        }
        if(!TextFieldContraseña.getText().equals(TextFieldContraseña2.getText())){
            setErrorMessage("Passwords doesn't match!");            
        }
    }
    
    public void loadWindow(ActionEvent event) throws IOException{ 
        
        Node node = (Node) event.getSource();        
        stage = (Stage) node.getScene().getWindow();
        StageData data = (StageData) stage.getUserData();        
        stage.close();  
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../vistas/unoLogin.fxml"));
        root = loader.load();         
        stage.setUserData(data);
                
        scene = new Scene(root); 
        stage.setScene(scene);
        stage.setTitle("Login");
        
        LoginController controller = loader.getController();    
        data.partida.setController(controller);                //Actualizamos controlador de Partida
        controller.setInitState();                             //porque será la siguiente ventana en mostrarse
        stage.show();                                          //y requerimos ejecutar metodos en partida
                                                               //que hacen uso de este controlador
    }    
    
    //Notifica al usuario de Error de registro
    public void setErrorMessage(String msg){
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        spinner.setVisible(false);        
    }
    
    //Estado inicial del spinner y el errorLabel
    public void setInitState(){
        errorLabel.setVisible(false);
        spinner.setVisible(false);
    }
                
    
}




//NOTA:  [Documentacion anterior]
    //Cuando cerramos una ventana la clase se destruye.
    //Entonces si pasamos de una ventana a otra la anterior se destruye con todos los datos
    //Requerimos primero obtener los datos para pasarlos a la siguiente ventana
    //antes de que la actual se cierre
    //ver https://dev.to/devtony101/javafx-3-ways-of-passing-information-between-scenes-1bm8
