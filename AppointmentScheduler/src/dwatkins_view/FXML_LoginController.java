/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dwatkins_view;

import dwatkins_model.Appointment;
import drwatkins_util.DBConnection;
import dwatkins_model.Session;
import dwatkins_model.User;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author David Watkins
 */
public class FXML_LoginController implements Initializable {
    
    ResourceBundle resource = ResourceBundle.getBundle("drwatkins_resources/Login", Locale.getDefault());
    private static final Logger LOGGER = Logger.getLogger(FXML_LoginController.class.getName());
    private FileHandler fileHandler = null;
    
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Label programTitleLabel, usernameLabel, passwordLabel, loginErrorText;
    
    @FXML
    private Menu fileLabel;

    @FXML
    private MenuItem exitLabel;

    @FXML
    void exitProgram(ActionEvent event) {
        DBConnection.close();
        System.exit(0);
    }
    
    /**
     * Validates username and password by making sure the fields are not blank
     * and then comparing the fields to the database. If a match is found,
     * login() is called with the current user as the sole argument.
     * NOTE: To fulfill requirements, this method employs Try with Resources
     * blocks as a form of Exception Control for the username/password.
     * @param event
     * @throws IOException
     * @throws SQLException 
     */    
    @FXML
    private void validateLogin(ActionEvent event) throws IOException, SQLException {
        
        if (usernameField.getText().isEmpty() && passwordField.getText().isEmpty()) {
            loginErrorText.setText(resource.getString("noCredentials"));
            return;
        } else if (usernameField.getText().isEmpty()) {
            loginErrorText.setText(resource.getString("noUsername"));
            return;
        } else if (passwordField.getText().isEmpty()) {
            loginErrorText.setText(resource.getString("noPassword"));
            return;
        }
        
        String sql = "SELECT userId, userName, password FROM user WHERE userName = ? AND password = ?";
        Connection connection = DBConnection.getConnection();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, usernameField.getText());
            statement.setString(2, passwordField.getText());
            
            try (ResultSet results = statement.executeQuery()) {

                if (results.next()) {
                    int userID = results.getInt("userId");
                    String username = results.getString("userName");
                    String password = results.getString("password");
                
                    User currentUser = new User(userID, username, password);
                    Session.getInstance().setUser(currentUser);
                    login(currentUser);
                } else {
                    loginErrorText.setText(resource.getString("invalidLogin"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("SQL Error");
            throw new SQLException("Error in SQL Query (" + sql + ")\n" + ex.getMessage());
        }      
    }
    
    private void login(User user) throws IOException {
        if (user != null) {
            LOGGER.log(Level.INFO, "{0} logged in.", user.getUsername());
            Parent root = FXMLLoader.load(getClass().getResource("/dwatkins_view/FXML_Main.fxml"));
            Scene main = new Scene(root);
                                
            Stage stage = Session.getInstance().getStage();
            stage.setScene(main);
            stage.setTitle("Appointment Scheduling System");
            stage.setResizable(false);
            
            appointmentReminder(); 
        }
    }
    
    public Appointment getAppointmentWithin15() {
        ObservableList<Appointment> appointmentList = Session.getInstance().getAppointmentList();
        LocalDateTime now = LocalDateTime.now();

        for (int x = 0; x < appointmentList.size(); x++) {
            LocalDateTime apptStart = LocalDateTime.parse(appointmentList.get(x).getStartDateTime(), Session.DATE_TIME);
            String consultant = appointmentList.get(x).getConsultant();

            if (apptStart.isAfter(now) && apptStart.isBefore(now.plusMinutes(15)) && consultant.equals(Session.getInstance().getUser().getUsername())) {
                return appointmentList.get(x);
            }
        }        
        return null;
    }
    
    public void appointmentReminder() {
        System.out.println("Checking for upcoming appointments...");
        Appointment imminentAppt = getAppointmentWithin15();
        
        if (imminentAppt != null) {
            LocalDateTime apptDateTime = LocalDateTime.parse(imminentAppt.getStartDateTime(), Session.DATE_TIME);
            String startTime = apptDateTime.toLocalTime().format(Session.TIME_FORMAT);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Alert");
            alert.setHeaderText(null);
            alert.setContentText("You have an upcoming appointment at " + startTime + " with " + imminentAppt.getCustomerName() + ".\n\n"
                + "Please check your appointment list for more details.");
            alert.showAndWait();            
        } else {
            System.out.println("NO appointments found within next 15 minutes.");
        }
    }
   
    /**
     * Initializes the Login controller.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Setup logging to track user activity.
        try {
            fileHandler = new FileHandler("userActivity.log", true);
        } catch (SecurityException | IOException ex) {
            ex.printStackTrace();
        }
        
        fileHandler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(fileHandler);
        LOGGER.setLevel(Level.INFO);

        programTitleLabel.setText(resource.getString("programTitle"));
        usernameLabel.setText(resource.getString("username"));
        passwordLabel.setText(resource.getString("password"));
        loginButton.setText(resource.getString("loginButton"));
        fileLabel.setText(resource.getString("file"));
        exitLabel.setText(resource.getString("exit"));
        
        // Clear login error text when username value changes.
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginErrorText.setText("");
        });
        
        // Clear login error text when password value changes.
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginErrorText.setText("");
        });
    }    
}