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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author anahi
 */
public class GameController {
    
    public Stage stage; 
    
    @FXML
    private ImageView Toma_carta;
    @FXML
    private Pane Seleccion_color;
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
    private Label jugador1;
    @FXML
    private Label jugador2;
    @FXML
    private Label jugador3;
    @FXML
    private Label jugador4;
   
    
    public void inicializarPartida(){                         
        Seleccion_color.setDisable(true);
        Seleccion_color.setVisible(false);
        nombre_ganador.setVisible(false);
        Toma_carta.setDisable(true);   
        //insertarJugadores();
    }
    
    
    
    /*Documentacion
    https://stackoverflow.com/questions/16990482/java-lang-illegalargumentexception-invalid-url-or-resource-not-found
    */
    
    //Recibe una lista de Strings, con los numeros de las cartas a renderizar
    //Por ejemplo {1,2,4,5,19,6,12,30}
    public void cardClick(String cardId) {
        List<String> params = new ArrayList<>();
        params.add("ok");
        params.add(cardId);
        Message msg = new Message("K", 0, params);
        
        /*
        StageData data = (StageData) stage.getUserData();
        data.connection.sendMessage(msg);
        */
        
    }
    
    public void showUsernames( List<String> usernames){
         jugador1.setText(usernames.get(0));
         jugador2.setText(usernames.get(1));
         if(usernames.size()>=2){
             jugador3.setText(usernames.get(2));
             if(usernames.size()==3){
             jugador4.setText(usernames.get(3));     
            }else{
                 jugador4.setText("");
             }
         }else{
             jugador3.setText("");
             jugador4.setText("");
         }
    }
    
     public void habilitaSeleccionColor(){
         Seleccion_color.setDisable(false);
        Seleccion_color.setVisible(true);
     }
    
    public void renderTurnoActual(String turnoActual){
        Nombre_turno_actual.setText(turnoActual); 
    } 
    
    public void renderCartaActual(int cartaMesa){
         Image image1 = new Image("..\\Cartas\\"+cartaMesa+".png");
        Carta_Juego.setImage(image1);
    } 
    
    
    public void renderUNOCards(List<Integer> cards){
        String imagePath = "../cartas/";
        
        GridPane gridPane = new GridPane(); //Cuadricula 7xN 
        gridPane.setVgap(10);  //Margenes de las cartas
        gridPane.setHgap(10);        
        
        
        //Agregar 7 cartas por renglon
        int col = 0, row = 0;        
        for (int card : cards) {
            String path = imagePath + card + ".png";                        
            System.out.println(path);
            ImageView imageView1 = new ImageView(getClass().getResource(path).toExternalForm()); 
            imageView1.setOnMouseClicked(event -> {
                // Evento de click en la carta
                cardClick( Integer.toString(card));
            });
            imageView1.setId(Integer.toString(card));
            imageView1.setFitWidth(74); 
            imageView1.setFitHeight(110);
                       
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
    }
    public void presionaBotonVerde(ActionEvent event) throws Exception{
        List<String> msg = new ArrayList<>();
        msg.add("ok");
        msg.add("Verde");
        //manda numero 
        Message message= new Message("Q", -1,msg);
        StageData data = (StageData) stage.getUserData();
        data.connection.sendMessage(message);
    }
    public void presionaBotonAmarillo(ActionEvent event) throws Exception{
        List<String> msg = new ArrayList<>();
        msg.add("ok");
        msg.add("Amarillo");
        //manda numero 
        Message message= new Message("Q", -1,msg);
        StageData data = (StageData) stage.getUserData();
        data.connection.sendMessage(message);
    }
    public void presionaBotonAzul(ActionEvent event) throws Exception{
        List<String> msg = new ArrayList<>();
        msg.add("ok");
        msg.add("Azul");
        //manda numero 
        Message message= new Message("Q", -1,msg);
        StageData data = (StageData) stage.getUserData();
        data.connection.sendMessage(message);
    }
     
     
     
     
     
    
}
