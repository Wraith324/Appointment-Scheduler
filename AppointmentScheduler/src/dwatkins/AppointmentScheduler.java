/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dwatkins;

import drwatkins_util.DBConnection;
import dwatkins_model.Session;
import java.util.Locale;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author David Watkins
 */
public class AppointmentScheduler extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/dwatkins_view/FXML_Login.fxml"));
        Scene login = new Scene(root);
        
        Session.getInstance().setStage(stage);
        stage.setScene(login);
        stage.setTitle("Appointment Scheduling System - Login");
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Locale.setDefault(new Locale("es", "MX")); // Spanish Language
        //Locale.setDefault(new Locale("pt", "BR")); // Portuguese Language
        DBConnection.start();
        launch(args);
        DBConnection.close();
    }    
}