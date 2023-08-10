
package serveruno;

import ClientServer.Message;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*
Clase para representar una sala de juego, se pueden unir max. 4 jugadores
Funciones:
*Acceso a nombre, id y usuarios conectados en la sala
*Agregar/Eliminar un jugador de la sala
*Enviar actualizaciones de movimientos del juego
 de otros usuarios


En esta clase se pueden importar los metodos para repartir cartas o como tal crear aqui una instancia del 
objeto que las contiene


*El servidor [Hilo principal] actualmente solo deberia encargarse de escuchar NUEVAS conexiones de clientes
*Pero esta procesando la partida al mismo tiempo que escucha NUEVAS conexiones
*No hay un hilo exclusivo donde la partida esté corriendo
*Lo mejor seria crear un hilo y asignarle la sala, para que procese todos los movimientos de los usuarios
*El unico problema de esto es solucionar la sincronizacion?
*
*
*/
public class GameRoom{
    private String id;
    private int activeUsers;
    private String rName;
    private List<ClientThread> players;//o usuarios, es lo mismo
    
    //JUEGO
    Carta[] diccionario= defineDictionary(); //diccionario
    ServerCards juegoCartas; //baraja de comida
    int cartaMesa; // mesa actual 
    int aumento; //como aumenta el turno 
    int turno; //persona con actual turno
    boolean bandera; 
    
    //cuando toquen el boton de inicializar partida
     /*
    INICIO DEL JUEGO
    -> inicializa diccionario cartas
    -> Inicializa baraja
    -> Brinda turnos 
    -> Brinda a cada usuario cartas 
    -> elije carta mesa;
    */
    
    // si no esta conectado, no le pase las cartas
    // si no estaba conectado y ahora si, le pase las cartas 
    //si no esta conectado y es su turno, comer cartas automaticamente
    // revisar si el hilo se pierde cuando se desconecta 
    
    
    public GameRoom(String name, String id){
        players = new ArrayList<>();
        activeUsers = 0;
        rName = name;
        this.id = id;
    }    
    public String getroomName(){
        return rName;
    }
    public int getActiveUsers(){
        return activeUsers;
    }
    public String getRoomId(){
        return id;
    }
    //Agrega un usuario a la sala
    public Boolean joinUser(ClientThread newUser){
        if(activeUsers < 4){
           players.add(newUser);
           activeUsers++; 
           return true;
        }
        return false;
    }
    
    public Carta[] defineDictionary(){
        Carta[] juegoCartas = new Carta[62];  
        int cont=0;
        String[] colores=new String[] {"Rojo", "Verde","Amarillo", "Azul"};
        
        for(int i=0; i<4; i++){
            for(int j=0; j<10;j++){
                juegoCartas[cont]= new Carta(colores[i],1, j);// clave vacia 40
                cont++;
            }
            
            juegoCartas[cont]= new Carta(colores[i],0 ,10 ); //cartaReversa;
            cont++;
            juegoCartas[cont]= new Carta(colores[i],0 ,11); //cartaSum2
            cont++;
            juegoCartas[cont]= new Carta(colores[i],0 ,12 ); //cartaBloqueo;
               // clave vacia 52 
            cont++;
        }
        juegoCartas[cont]= new Carta("Negro",0 ,13); //52
        cont++;
         juegoCartas[cont]= new Carta("Negro",0 ,14);//53
        cont++;
        
        for(int i=0; i<4; i++){
           juegoCartas[cont]= new Carta(colores[i],0 ,13); // 58 vacia;
           cont++;
        }
        
        for(int i=0; i<4; i++){
           juegoCartas[cont]= new Carta(colores[i],0 ,14); // 62 vacia;
           cont++;
        } 
        //vacia 62
        
        return juegoCartas; 
    }
    
    public void  inicializaPartida() throws IOException{
       //diccionario= defineDictionary();
       juegoCartas= new ServerCards();
       List<String> message= new ArrayList<>();
       message.add("ok");
       aumento=1; 
       turno=0;
       
       bandera= false; 
       
       for(int i=0; i<players.size(); i++){
           message.add(players.get(i).getUsername());
           darTurno(i);
           darJuegoCartas(i);   
       }
       cartaMesa= juegoCartas.darComida(); 
       Message msg = new Message("H", -1,message);                              //Envia Lista de jugadores
       sendMessageToRoomMembers(msg);
       inicializarInfo();                                                       //Envia carta mesa/turno actual/bandera
    }
    
    //este se supone que sirve para actualizar a todos en el servidor
    //debe de estar relacionado a la funcion de nuevo turno
    //sinfuciona como esta pensado debe de cambiarse donde se llama inicializar Info 
    //y en clienthandler se quitaria nuevoTurno (cambiariamos) , y ya no se usaria esta funciom
    //aun no lo cambio porque tiene error de la excepcion que falta completar
    public void reenvioInformacionUsuario() throws IOException{
        
       List<String> message= new ArrayList<>();
       message.add("ok");
       System.err.println("jugadores"+ players.size());
       //no se porque debo poner size ------------------------------------------------------
       //SI imprimo player.size me sale 0; eso quiere decir que ya no existe ni un jugador 
       //y es por eso q no me imprime ni un username
       for(int j=0; j<players.size(); j++){
           message.add(players.get(j).getUsername());
           //Nombre jugador
           System.err.println(players.get(j).getUsername());
        }
            Message msg = new Message("H", -1,message);                              //Envia Lista de jugadores
            sendMessageToRoomMembers(msg);
       
       for(int j=0; j<players.size(); j++){
           darTurno(j);  //mandamos turnos especificos
           reenvioCartasU(j);
        }
       
       inicializarInfo();//manda turno actul, carta mesa, bandera
        System.err.println("turno actual"+ turno); // esto si lo imprime bien 
    }
    
   public void reenvioCartasU(int j) throws IOException{
       ClientThread player = players.get(j);
             List<String> cartas = new ArrayList<>();
             cartas.add("ok");
            for(int i=0; i<player.getSizeCartas(); i++){
                cartas.add(Integer.toString(player.getCarta(i)));
            }
            Message message= new Message("G", -1, cartas );
            player.sendMessage(message); 
       
   }
    public void inicializarInfo() throws IOException{
        List<String> message = new ArrayList<>();
        message.add("ok");
        message.add(Integer.toString(cartaMesa));
        message.add(Integer.toString(turno)); // mando turno actual 
        message.add(Boolean.toString(bandera));
         //carta actual
        Message msg = new Message("J", -1, message );
        sendMessageToRoomMembers(msg);  
    }
    
        //manda a cada jugador su turno al inicializar partida
    public void darTurno(int t) throws IOException{
        List<String> turn = new ArrayList<>();
        turn.add("ok");
        turn.add(Integer.toString(t));
        Message message= new Message("F", -1,turn);
        players.get(t).sendMessage(message);
    }
    
    //manda a cada jugador 7 cartas
    //agrega al arreglo de cartas en el servidor 
    public void darJuegoCartas(int i) throws IOException{
        int num;
        ClientThread player = players.get(i);
        List<String> cartas = new ArrayList<>();
        cartas.add("ok");
        player.emptyCartas(); 
        for(int j=0; j<7; j++){
            num = juegoCartas.darComida();  
            player.setCarta(num);//damos cartas
            cartas.add(Integer.toString(num));  
        } 
        Message message= new Message("G", -1, cartas );
        player.sendMessage(message);        
    }
    
        //regresa numero del juego de cartas
    public String getComida(){
        int carta= juegoCartas.darComida();
        ClientThread player = players.get(turno);
        player.setCarta(carta);
        return Integer.toString(carta);        
    }
    
        //hace el castigo de usuario 
    public List<String> efectoSigUsuario() throws IOException{
        List<String> message= new ArrayList<>();
        message.add("ok");
       switch(diccionario[cartaMesa].getSimbolo()){
           case 11:
               //salta
               //message.add(Integer.toString(-1));
               message.add(getComida());
               message.add(getComida());
               
               break;
            case 14:
                //salta
               //message.add(Integer.toString(-1));
               message.add(getComida());
               message.add(getComida());
               message.add(getComida());
               message.add(getComida());
               break;
               /*
           case 12:
               message.add(Integer.toString(-1));
               //no haces nada
               break;   
               */
       } 
       ///tiene q ser otra cosa 
       bandera=false;
      return message; 
    }
    //mandar mensaje nueva carta
    public void nuevaCarta() throws IOException{
        List<String> message = new ArrayList<>();
        message.add("ok");
        message.add(Integer.toString(cartaMesa)); //carta actual
        Message msg = new Message("M", -1, message );
        sendMessageToRoomMembers(msg);  
    }
    public void nuevoTurno() throws IOException{
       List<String> message = new ArrayList<>();
        message.add("ok");
        message.add(Integer.toString(turno)); //carta actual
        message.add(Boolean.toString(bandera));
        Message msg = new Message("N", -1, message );
        sendMessageToRoomMembers(msg);  
    }
    //Revisar, devuelve turno 49 para 2 usuarios
    public void aumentoTurno(){
        if(turno+aumento>players.size()-1){
           turno= 0;
        }else if(turno+aumento<0){
           turno= players.size()-1;
        }else{
           turno= turno + aumento;
        } 
    }
    
    
    public void eliminarCartaCliente(int num) throws IOException{
        
        ClientThread player = players.get(turno);
        player.elimCarta(num);
        
        juegoCartas.regresaCartaUsuario(num);
        reenvioCartasU(turno);
    }

         //avisa si la carta es correcta
    public int chequeoCarta(int carta) throws IOException{
        if(diccionario[carta].getColor().equals(diccionario[cartaMesa].getColor())
                || diccionario[carta].getSimbolo()==diccionario[cartaMesa].getSimbolo()
                || diccionario[carta].getColor().equals("Negro")){
        cartaMesa=carta;
        //mandar mensaje de que elimine carta en usuario
        eliminarCartaCliente(carta);
        //y muestra nueva carta
        nuevaCarta();
        if(players.get(turno).getSizeCartas()==0){
            List<String> message = new ArrayList<>();
            message.add("ok");
           Message msg = new Message("T", -1, message );
           sendMessageToRoomMembers(msg);
           return 2;
        }
        if ( diccionario[cartaMesa].esCartaEspecial()){// si la crta es especial 
            return efectosUsuario();
        }   
        /*
            aumentoTurno();
            nuevoTurno();*/
            return 1;
        }
        return 0; 
    }
    /*
    0= no es carta correcta
    1= es carta correcta y aumenta turno
    -1 = activa boton
    */
    
    //manda mensaje de eliminar carta en usuario 

   
    public int efectosUsuario(){
        if (diccionario[cartaMesa].getSimbolo()==11
               || diccionario[cartaMesa].getSimbolo()==12
               || diccionario[cartaMesa].getSimbolo()==14){
            bandera= true;
        }
        
        if(diccionario[cartaMesa].getSimbolo()==14 
                || diccionario[cartaMesa].getSimbolo()==13){
            return -1; 
            //paso extra, debe el mismo usuario tocar un boton
        }else if ( diccionario[cartaMesa].getSimbolo()==10 ){
        aumento=aumento*-1; } 
        return 1;
    }
    
    // cambio en el caso que tenga que elegir boton 
    public void cambioColorCartaEspecial(String opBoton) throws IOException{
            
        if( diccionario[cartaMesa].getSimbolo()==13){ //caso que sea boton cambio color 
            switch(opBoton){
                case "Amarillo" -> cartaMesa=56;
                case "Azul" -> cartaMesa=57;
                case "Rojo" -> cartaMesa=54;
                case "Verde" -> cartaMesa=55;  
            }   
        }else{ //caso que sea boton +4
            switch(opBoton){
                case "Amarillo" -> cartaMesa=60;
                case "Azul" -> cartaMesa=61;
                case "Rojo" -> cartaMesa=58;
                case "Verde" -> cartaMesa=59;  
            }   
        }
        aumentoTurno();
        inicializarInfo();
        
    }  
    
    
    //Metodo para enviar actualizaciones del estado del juego a los miembros de la sala
    public void sendMessageToRoomMembers(Message msg) throws IOException{
        for (ClientThread player : players) {
            if(!player.getSocket().isOutputShutdown())
                player.sendMessage(msg);
        }
    }
    //Elimina un jugador de la sala
    public void removeUserFromRoom(ClientThread usr){        
        players.remove(usr);
        if(activeUsers > 0)
            activeUsers--;        
    }
    
    public void removeUserCardsFromGame(ClientThread usr){
        for(int i=0; i<usr.getSizeCartas(); i++){
            juegoCartas.regresaCartaUsuario(usr.getCarta(i));  
        }        
        if(turno> players.size()-1){
           aumentoTurno();
        }
    }
    
    public Boolean isPlayerInRoom(Socket s){
        for (ClientThread player : players) {
            if(player.getSocket().getInetAddress().equals(s.getInetAddress())){
                return true;
            }
        }
        return false;
    }
}
