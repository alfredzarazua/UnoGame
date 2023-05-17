
package clientuno.controladores;
import ClientServer.Message;
import clientuno.modelo.StageData;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

public class InstructionsController {
    private Stage stage;
    
    @FXML
    public ProgressIndicator spinner; 
    
    public void homeButton(ActionEvent event) throws IOException{
        
        spinner.setVisible(true);
        LoginController controller = new LoginController();                
        Node node = (Node) event.getSource();        
        stage = (Stage) node.getScene().getWindow();
        controller.stage = stage;  
        
                
        StageData data = (StageData) stage.getUserData();
        Message ds = new Message("E", -1, null); 
        data.connection.sendMessage(ds); 
        data.partida.setController(controller);
        //Se envia la solicitud para obtener la lista de salas
        //El evento automaticamente abrirá la ventana de inicio
        //una vez recibidos los parametros
    }
    
    public void setInitState(){        
        spinner.setVisible(false);
    }
}
