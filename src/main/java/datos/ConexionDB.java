package datos;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConexionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/prueba";
    private static final String USER = "root";
    private static final String PASSWORD = "2204";
    
    
    public static Connection AbrirConexion(){
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("¡Conexión exitosa!"); // Debug
            return conn;
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null,
                    "Error de conexión: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }







    public static void CerrarConexion(Connection conn){
        if(conn != null){
            try{
                conn.close();
            }catch(SQLException ex){
                System.out.println("Error al cerrar conexión: " + ex.getMessage());
            }
        }
    }
}
