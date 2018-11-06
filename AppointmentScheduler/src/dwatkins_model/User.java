/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dwatkins_model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author David Watkins
 */
public class User {
    private IntegerProperty userID;
    private StringProperty username;
    private StringProperty password;
    
    public User(int userID, String username, String password) {
        this.userID = new SimpleIntegerProperty(userID);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);        
    }
    
    public User() {
        
    }
    
    public void setUerID(int userID) {
        this.userID.set(userID);
    }
    
    public int getUserID() {
        return this.userID.get();
    }
    
    public void setUsername(String username) {
        this.username.set(username);
    }
    
    public String getUsername() {
        return this.username.get();
    }
    
    public void setPassword(String password) {
        this.password.set(password);
    }
    
    public String getPassword() {
        return this.password.get();
    }    
}