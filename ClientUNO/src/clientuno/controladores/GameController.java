/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clientuno.controladores;


import ClientServer.Message;
import clientuno.modelo.StageData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author anahi
 */
public class GameController {
    
    public Stage stage; 
    
    @FXML
    private Button Toma_carta;
    @FXML
    private Pane Seleccion_color_pane;    
    @FXML
    private Pane namesListPane;  
    @FXML
    private ScrollPane Cartas_juego;
    @FXML
    private Label nombre_ganador;
    @FXML
    private Label Nombre_turno_actual;
    @FXML
    private ImageView Carta_Juego; 
    @FXML 
    private Button Salir_Sala; // no se como hacerlo     
    @FXML
    private Label roomName;
    
    @FXML
    private GridPane gridPane; 
   
    
    public void inicializarPartida(){                         
        deshabilitaColorPane();
        nombre_ganador.setVisible(false);
       
        Tooltip tooltip = new Tooltip("Tomar una carta");
        Tooltip.install(Toma_carta, tooltip);
        
        Toma_carta.setDisable(true);  
        gridPane.setDisable(true); 
        //insertarJugadores();
    }
   
    public void visibleNombreGanador(){
        nombre_ganador.setVisible(true);
    }
    
    public void habilitaTurno(){
        Toma_carta.setDisable(false);  
        gridPane.setDisable(false); 
    }
    
    
    public void deshabilitaColorPane(){
        Seleccion_color_pane.setVisible(false);
        Seleccion_color_pane.setDisable(true);
    }
    
    public void habilitaColorPane(){
        Seleccion_color_pane.setVisible(true);
        Seleccion_color_pane.setDisable(false);
     }
    
    public void renderNombreGanador(String text){
        nombre_ganador.setText(text);
        nombre_ganador.setVisible(true);
    }
    
    /*Documentacion
    https://stackoverflow.com/questions/16990482/java-lang-illegalargumentexception-invalid-url-or-resource-not-found
    */
    
    //Recibe una lista de Strings, con los numeros de las cartas a renderizar
    //Por ejemplo {1,2,4,5,19,6,12,30}
    public void cardClick(String cardId) throws IOException {
        List<String> params = new ArrayList<>();
        params.add("ok");
        params.add(cardId);
        Message msg = new Message("K", 0, params);
                
        
        StageData data = (StageData) stage.getUserData();
        data.connection.sendMessage(msg);
    }
    
    public void showUsernames( List<String> usernames){   
        
        StageData data = (StageData) stage.getUserData();
        Label nameLabel;
        double x = 15, y = 35;
        for (String username : usernames) {
            if(username.equals(data.username)){
                nameLabel = new Label(username + " (You)");                                
            }else{
                nameLabel = new Label(username);
            }
            nameLabel.setLayoutX(x);
            nameLabel.setLayoutY(y);
            nameLabel.setFont(new Font(12));
            nameLabel.setTextFill(Color.BLACK);
            y += 22;
            namesListPane.getChildren().add(nameLabel);
        }
    }
    
    //Revisar si funciona el turno
    //Se requiere actualizar: la carta actual o en juego
    //                        la carta de comida?
    //                        la etiqueta de turno

    public void renderTurnoActual(String turnoActual){
        Nombre_turno_actual.setText(turnoActual);
    } 
    
    public void renderCartaActual(int cartaMesa){
        String path = "../cartas/"+cartaMesa+".png";
        Image image1 = new Image(getClass().getResource(path).toExternalForm());        
        Carta_Juego.setImage(image1);
    } 
    
    public void setRoomName(String name){
        roomName.setText(name);
    }
    
    
    public void renderUNOCards(List<Integer> cards){
        String imagePath = "../cartas/";
        
        gridPane = new GridPane(); //Cuadricula 7xN 
        gridPane.setVgap(10);  //Margenes de las cartas
        gridPane.setHgap(10);        
        
        
        //Agregar 7 cartas por renglon
        int col = 0, row = 0;        
        for (int card : cards) {
            String path = imagePath + card + ".png";                                    
            ImageView imageView1 = new ImageView(getClass().getResource(path).toExternalForm()); 
            imageView1.setOnMouseClicked(event -> {
                try {
                    // Evento de click en la carta
                    cardClick( Integer.toString(card));
                } catch (IOException ex) {
                    Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            imageView1.setId(Integer.toString(card));
            imageView1.setFitWidth(74); 
            imageView1.setFitHeight(110);
            imageView1.setCursor(Cursor.HAND);
            Tooltip tooltip = new Tooltip("Poner Carta");
            Tooltip.install(imageView1, tooltip);
                       
            gridPane.add(imageView1, col, row);
                        
            col ++;
            if(col > 6){
                col = 0;
                row ++;                
            }            
        }
    
        Cartas_juego.setContent(gridPane);
    }
    
    public void presionaBotonRojo(ActionEvent event) throws Exception{
        List<String> msg = new ArrayList<>();
        msg.add("ok");
        msg.add("Rojo");
        //manda numero 
        Message message= new Message("Q", -1,msg);
        StageData data = (StageData) stage.getUserData();
        data.connection.sendMessage(message);
        
        deshabilitaColorPane();
    }
    public void presionaBotonVerde(ActionEvent event) throws Exception{
        List<String> msg = new ArrayList<>();
        msg.add("ok");
        msg.add("Verde");
        //manda numero 
        Message message= new Message("Q", -1,msg);
        StageData data = (StageData) stage.getUserData();
        data.connection.sendMessage(message);
        
        deshabilitaColorPane();
    }
    public void presionaBotonAmarillo(ActionEvent event) throws Exception{
        List<String> msg = new ArrayList<>();
        msg.add("ok");
        msg.add("Amarillo");
        //manda numero 
        Message message= new Message("Q", -1,msg);
        StageData data = (StageData) stage.getUserData();
        data.connection.sendMessage(message);
        
        deshabilitaColorPane();
    }
    public void presionaBotonAzul(ActionEvent event) throws Exception{
        List<String> msg = new ArrayList<>();
        msg.add("ok");
        msg.add("Azul");
        //manda numero 
        Message message= new Message("Q", -1,msg);
        StageData data = (StageData) stage.getUserData();
        data.connection.sendMessage(message);
        
        deshabilitaColorPane();
    }
    
    public void presionaBotonComida(ActionEvent event) throws Exception{
        List<String> msg = new ArrayList<>();
        msg.add("ok");        
        //manda numero 
        Message message= new Message("O", -1,msg);
        StageData data = (StageData) stage.getUserData();
        data.connection.sendMessage(message);
    }
    
    
    
     
     
     
     
     
    
}
