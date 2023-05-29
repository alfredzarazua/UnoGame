package ClientServer;

import java.io.Serializable;
import java.util.List;


public class Message implements Serializable {
    public String eventID;
    public int userId;
    public List<String> parameters;
    
    public Message(String eventId, int user_id, List<String> param){
        this.eventID = eventId;
        this.userId = user_id;
        this.parameters = param;
    }
}


//Create and use a JAVA Class Library
//Visit: https://stackoverflow.com/questions/42301271/create-a-class-library-in-netbeans