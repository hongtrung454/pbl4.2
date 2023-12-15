package database;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.sql.Connection;
import java.sql.DriverManager;
import com.mysql.cj.jdbc.Driver;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author DELL
 */
public class JDBCUtil {
    public static Connection getConnection() {
        Connection c=null;
        
        
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            String url = "jdbc:mySQL://localhost:3306/filesyncdb";
            String username = "root";
            String password ="1234";
            
            c = DriverManager.getConnection(url, username, password);
            
        } catch (SQLException ex) {
            Logger.getLogger(JDBCUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return c;
    }
    public static void closeConnection(Connection c) {
        try {
            if (c!= null) {
            c.close();
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
