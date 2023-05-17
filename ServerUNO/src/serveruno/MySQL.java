package serveruno;

import Conectar.ConectarMySQL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.sql.ResultSet;

public class MySQL extends ServerUNO{
    private Statement st=null;
    private ResultSet rs=null;
        
    
    //Consultar datos y verificar usuario valido
    public String Login(List<String> params) throws SQLException{
        
        String Id_user, contraseña, resp="";
        contraseña = params.get(1);
        Id_user = params.get(0);
        ConectarMySQL conecta = new ConectarMySQL();
        Connection connection = conecta.getConnection();
        st = connection.createStatement();
        
        //usuario correcto y contraseña incorrecta no muestra mensaje de error       
       try{
            String query = "SELECT Contraseña FROM catalogo_usuarios WHERE Id_usuario =  '".concat(Id_user).concat("'");            
            rs = st.executeQuery(query);
        
            if(rs.next()){
                String contraseña_user= rs.getString(1);
                if(contraseña_user.equals(contraseña)){
                    System.out.println("\tNew User Logged in");
                    resp="ok";
                }else{
                    resp="Invalid user or password";
                }              
            }else{
                System.out.println("\tInvalid user or password");
                resp="Invalid user or password";
            }
                   
        }catch(SQLException e){      
            resp = "There was an error: " + e.getMessage();
        }
        return resp;
    }

    public String InsertarDatosRegistro(List<String> params) throws SQLException{
        //Hacer la comparación del usuario inicial 
        //if/else
        String Nombre, Contraseña, Id_user, resp = ""; 
        Id_user = params.get(0);
        Nombre = params.get(1);
        Contraseña = params.get(2);                        
        
        ConectarMySQL conecta = new ConectarMySQL();
        Connection connection = conecta.getConnection();
        st = connection.createStatement();                
        try{ 
            //Primero revisamos que el usuario no exista en la base de datos
            String query = "SELECT Nombre FROM catalogo_usuarios WHERE Id_usuario =  '".concat(Id_user).concat("'");            
            rs = st.executeQuery(query);
            if(rs.next()){
                resp = "Error: User already exists";
                System.out.println("\tError: User already exists in DB");
            }else{
                String query2 = "INSERT INTO catalogo_usuarios(Id_usuario,Nombre,Contraseña) VALUES ('"+Id_user+"',"+"'"+Nombre+"',"+"'"+Contraseña+"')";
                st.executeUpdate(query2);
                System.out.println("\tNew user added to DB"); 
                resp = "ok";
            }                                          
        }catch(SQLException e){
            resp = "Hubo un error";            
        }
        return resp;
    }
    
    
}
