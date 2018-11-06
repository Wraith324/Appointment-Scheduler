/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dwatkins_model;

import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

/**
 *
 * @author David Watkins
 */
public class Session {
    
    private static Session instance;
    
    private User user;
    private Stage stage;
    private final ObservableList<Customer> customerList;
    private final ObservableList<Appointment> appointmentList;
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("MM-dd-yyyy h:mm a");
    
    private Session() {
        customerList = FXCollections.observableArrayList();
        appointmentList = FXCollections.observableArrayList();
    } 
    
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public User getUser() {
        return this.user;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public Stage getStage() {
        return this.stage;
    }

    public void addCustomer(Customer customer) {
        customerList.add(customer);
    }
    
    public Customer getCustomer(int customerID) {
        for (int x = 0; x < customerList.size(); x++) {
            if (customerList.get(x).getCustomerID() == customerID) {
                return customerList.get(x);
            }
        }
        return null;
    }
    
    public ObservableList<Customer> getCustomerList() {
        return customerList;
    }
    
    public void clearCustomerList() {
        customerList.clear();
    }
    
    public void addAppointment(Appointment appointment) {
        appointmentList.add(appointment);
    }
    
    public Appointment getAppointment(int appointmentID) {
        for(int x = 0; x < appointmentList.size(); x++) {
            if(appointmentList.get(x).getAppointmentID() == appointmentID) {
                return appointmentList.get(x);
            }
        }
        return null;
    }
    
    public ObservableList<Appointment> getAppointmentList() {
        return appointmentList;
    }   
    
    public void clearAppointmentList() {
        appointmentList.clear();
    }
}
    

