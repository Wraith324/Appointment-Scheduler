/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dwatkins_model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author David Watkins
 */
public class Appointment {
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("MM-dd-yyyy h:mm a");
    
    private IntegerProperty appointmentID;
    private IntegerProperty customerID;
    private StringProperty customerName;
    private StringProperty title;
    private StringProperty type;
    private StringProperty description;
    private StringProperty location;
    private StringProperty consultant;
    private StringProperty startDateTime;
    private StringProperty endDateTime;
    private StringProperty date;
    private StringProperty startTime;
    private StringProperty endTime;    
    
    public Appointment(int appointmentID, int customerID, String customerName, String title, String type, String consultant, String startDateTime, String endDateTime) {
        this.appointmentID = new SimpleIntegerProperty(appointmentID);
        this.customerID = new SimpleIntegerProperty(customerID);
        this.customerName = new SimpleStringProperty(customerName);
        this.title = new SimpleStringProperty(title);
        this.type = new SimpleStringProperty(type);
        this.consultant = new SimpleStringProperty(consultant);
        this.startDateTime = new SimpleStringProperty(startDateTime);
        this.endDateTime = new SimpleStringProperty(endDateTime);
        this.date = new SimpleStringProperty(LocalDateTime.parse(startDateTime, DATE_TIME).toLocalDate().format(DATE_FORMAT));
        this.startTime = new SimpleStringProperty(LocalDateTime.parse(startDateTime, DATE_TIME).toLocalTime().format(TIME_FORMAT));
        this.endTime = new SimpleStringProperty(LocalDateTime.parse(endDateTime, DATE_TIME).toLocalTime().format(TIME_FORMAT));        
    }
    
    public Appointment(int appointmentID, int customerID, String customerName, String title, String type, String description, String location, String consultant, String startDateTime, String endDateTime) {
        this.appointmentID = new SimpleIntegerProperty(appointmentID);
        this.customerID = new SimpleIntegerProperty(customerID);
        this.customerName = new SimpleStringProperty(customerName);
        this.title = new SimpleStringProperty(title);
        this.type = new SimpleStringProperty(type);
        this.description = new SimpleStringProperty(description);
        this.location = new SimpleStringProperty(location);
        this.consultant = new SimpleStringProperty(consultant);
        this.startDateTime = new SimpleStringProperty(startDateTime);
        this.endDateTime = new SimpleStringProperty(endDateTime);
        this.date = new SimpleStringProperty(LocalDateTime.parse(startDateTime, DATE_TIME).toLocalDate().format(DATE_FORMAT));
        this.startTime = new SimpleStringProperty(LocalDateTime.parse(startDateTime, DATE_TIME).toLocalTime().format(TIME_FORMAT));
        this.endTime = new SimpleStringProperty(LocalDateTime.parse(endDateTime, DATE_TIME).toLocalTime().format(TIME_FORMAT));
    }
    
    public void setAppointmentID(int appointmentID) {
        this.appointmentID.set(appointmentID);
    }
    
    public int getAppointmentID() {
        return this.appointmentID.get();
    }
    
    public void setCustomerID(int customerID) {
        this.customerID.set(customerID);
    }
    
    public int getCustomerID() {
        return this.customerID.get();
    }
    
    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }
    
    public String getCustomerName() {
        return this.customerName.get();
    }
    
    public void setTitle(String title) {
        this.title.set(title);
    }
    
    public String getTitle() {
        return this.title.get();
    }
    
    public void setType(String type) {
        this.type.set(type);
    }
    
    public String getType() {
        return this.type.get();
    }
    
    public void setDescription(String description) {
        this.description.set(description);
    }
    
    public String getDescription() {
        return this.description.get();
    }
    
    public void setLocation(String location) {
        this.location.set(location);
    }
    
    public String getLocation() {
        return this.location.get();
    }
    
    public void setConsultant(String consultant) {
        this.consultant.set(consultant);
    }
    
    public String getConsultant() {
        return this.consultant.get();
    }
    
    public void setStartDateTime(String startDateTime) {
        this.startDateTime.set(startDateTime);
        setDate(startDateTime);
        setStartTime(startDateTime);
    }
    
    public String getStartDateTime() {
        return this.startDateTime.get();
    }
    
    public void setEndDateTime(String endDateTime) {
        this.endDateTime.set(endDateTime);
        setEndTime(endDateTime);
    }
    
    public String getEndDateTime() {
        return this.endDateTime.get();
    }    
    
    public void setDate(String startDateTime) {
        this.date.set(LocalDateTime.parse(startDateTime, DATE_TIME).toLocalDate().format(DATE_FORMAT));
    }
    
    public String getDate() {
        return this.date.get();
    }
    
    public void setStartTime(String startDateTime) {
        this.startTime.set(LocalDateTime.parse(startDateTime, DATE_TIME).toLocalTime().format(TIME_FORMAT));
    }
    
    public String getStartTime() {
        return this.startTime.get();
    }
    
    public void setEndTime(String endDateTime) {
        this.endTime.set(LocalDateTime.parse(endDateTime, DATE_TIME).toLocalTime().format(TIME_FORMAT));
    }
    
    public String getEndTime() {
        return this.endTime.get();
    }    
}
