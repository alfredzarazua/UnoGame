
package Conectar;

import java.sql.Connection;
import java.sql.DriverManager;


public class ConectarMySQL {
    public static final String URL = "jdbc:mysql://localhost:3306/juego_uno";
    public static final String USER = "root";
    public static final String PSWD = "1427";
    private Connection connection = null;
 
    public Connection getConnection(){        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = (Connection)DriverManager.getConnection(URL, USER, PSWD);            
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return connection;
    }
}
