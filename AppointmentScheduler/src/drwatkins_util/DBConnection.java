/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drwatkins_util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author David Watkins
 */
public class DBConnection {
      
    private static Connection connection;
    
    public DBConnection() {        
    }
    
    public static void start() {
        final String driver   = "com.mysql.jdbc.Driver";
        final String database = "U03VAs";
        final String url      = "jdbc:mysql://52.206.157.109/" + database;
        final String username = "U03VAs";
        final String password = "53688091311";
        
        try {
            System.out.println("Connecting to database: " + database);
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connection successful!");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
	} catch (ClassNotFoundException ex) {
            System.out.println("JDBC Driver not found.");
            ex.printStackTrace();
        }     
    }
    
    public static Connection getConnection() {
        return connection;
    }
    
    public static void close() {
        try {
            connection.close();
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            System.out.println("Connection closed successfully.");
        }        
    }    
}
