/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dwatkins_view;

import dwatkins_model.Appointment;
import dwatkins_model.Customer;
import drwatkins_util.DBConnection;
import dwatkins_model.Session;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author David Watkins
 */
public class FXML_RecordController implements Initializable {
    
    private final FadeTransition fade = new FadeTransition(Duration.millis(3000));    
    private final ObservableList<String> countries = FXCollections.observableArrayList();
    private final ObservableList<Appointment> custAppointments = FXCollections.observableArrayList();
    private FXML_MainController mainController;
    private static Connection connection = DBConnection.getConnection();
    
    @FXML
    private TextField nameField, addressField1, addressField2, cityField, zipField, phoneField, apptTitleField;
    
    @FXML
    private Button saveCustBtn, saveCloseCustBtn, cancelCustBtn, newApptBtn, saveApptBtn, deleteApptBtn, cancelApptBtn;
    
    @FXML
    private Label customerIDLabel, nameErrorText, addressErrorText, cityErrorText, countryErrorText, zipErrorText, phoneErrorText, statusMessage;
    
    @FXML
    private Label customerNameLabel, timeZoneLabel, apptIDLabel, apptStatusMessage, titleErrorText, typeErrorText, dateErrorText, durationErrorText;
    
    @FXML
    private ComboBox<String> apptTypeCombo, apptStartCombo, apptEndCombo, countryCombo;
        
    @FXML
    private DatePicker apptDatePicker;
        
    @FXML
    private TableView<Appointment> appointmentTable;
    
    @FXML
    private TableColumn<Appointment, String> apptIDColumn, apptTitleColumn, apptTypeColumn, apptStartColumn, apptEndColumn, apptConsultantColumn;

    @FXML
    private Tab customerTab, appointmentsTab;
    
    @FXML
    private TabPane recordTabs;
    
    @FXML
    void saveCustomer(ActionEvent event) {
        if (validateCustomer()) {
            return;
        }
        if (customerIDLabel.getText().equalsIgnoreCase("Auto Generated")) {
            newCustomer();
            statusMessage.setText("Customer added successfully!");
            fadeout(statusMessage);
        } else {
            modifyCustomer();
            statusMessage.setText("Customer modifications saved!");
            fadeout(statusMessage);
        }
        saveCustBtn.setDisable(true);
        saveCloseCustBtn.setDisable(true);
    }
    
    @FXML
    void saveCloseCustomer(ActionEvent event) {
        if (validateCustomer()) {
            return;
        }
        if (customerIDLabel.getText().equalsIgnoreCase("Auto Generated")) {
            newCustomer();
            // Close Window
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } else {
            modifyCustomer();
            // Close Window
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();            
        }
    }
    
    @FXML
    void newAppointment(ActionEvent event) {
        clearAppointmentFields();
        enableAppointmentFields();
        apptIDLabel.setText("Auto Generated");
        appointmentTable.getSelectionModel().clearSelection();
    }
    
    @FXML
    void saveAppointment(ActionEvent event) {
        if (validateAppointment()) {
            
            Timestamp startTime = convertToUTC(apptDatePicker.getValue(), apptStartCombo.getValue());
            Timestamp endTime = convertToUTC(apptDatePicker.getValue(), apptEndCombo.getValue());
            
            if (apptIDLabel.getText().equalsIgnoreCase("Auto Generated")) {
                if (!appointmentConflicts(startTime, endTime, 0)) {
                    addAppointment();
                    apptStatusMessage.setText("Appointment added successfully!");
                    fadeout(apptStatusMessage);
                    saveApptBtn.setDisable(true);
                }
            } else if (!appointmentConflicts(startTime, endTime, Integer.parseInt(apptIDLabel.getText()))) {
                modifyAppointment();
                apptStatusMessage.setText("Appointment modification saved!");
                fadeout(apptStatusMessage);
                saveApptBtn.setDisable(true);
            }
        }
    }

    @FXML
    void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    public void setMainController(FXML_MainController mainController) {
        this.mainController = mainController;
    }
    
    private void newCustomer() {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy)"
                    + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)", Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, addressField1.getText());
            statement.setString(2, addressField2.getText());
            statement.setInt(3, getCityID());
            statement.setString(4, zipField.getText());
            statement.setString(5, phoneField.getText());
            statement.setString(6, Session.getInstance().getUser().getUsername());
            statement.setString(7, Session.getInstance().getUser().getUsername());
            statement.execute();
            
            ResultSet results = statement.getGeneratedKeys();
            
            if (results.next()) {
                int addressID = results.getInt(1);
                System.out.println("Address Added Successfully!");
                System.out.println("Address ID: " + addressID);
            
                statement = connection.prepareStatement("INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy)"
                    + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)", Statement.RETURN_GENERATED_KEYS);
            
                statement.setString(1, nameField.getText());
                statement.setInt(2, addressID);
                statement.setInt(3, 1);
                statement.setString(4, Session.getInstance().getUser().getUsername());
                statement.setString(5, Session.getInstance().getUser().getUsername());
                statement.execute();
                results = statement.getGeneratedKeys();
                       
                if (results.next()) {
                    int customerID = results.getInt(1);
                    mainController.updateCustomerTable();
                    customerIDLabel.setText(Integer.toString(customerID));
                    initializeAppointmentTab(nameField.getText());
                    loadAppointment();
                }
            }
        } catch (SQLException ex) {
            System.out.println("SQL Error");
            ex.printStackTrace();
        }
    }
    
    private void modifyCustomer() {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE customer, address, city, country "
                + "SET customerName = ?, address = ?, address2 = ?, address.cityId = ?, postalCode = ?, phone = ?,"
                + "customer.lastUpdate = CURRENT_TIMESTAMP, customer.lastUpdateBy = ?, address.lastUpdate = CURRENT_TIMESTAMP, address.lastUpdateBy = ?"
                + "WHERE customerId = ? AND customer.addressId = address.addressId AND address.cityId = city.cityId AND city.countryId = country.countryId");
            
            statement.setString(1, nameField.getText());
            statement.setString(2, addressField1.getText());
            statement.setString(3, addressField2.getText());
            statement.setInt(4, getCityID());
            statement.setString(5, zipField.getText());
            statement.setString(6, phoneField.getText());
            statement.setString(7, Session.getInstance().getUser().getUsername());
            statement.setString(8, Session.getInstance().getUser().getUsername());
            statement.setInt(9, Integer.parseInt(customerIDLabel.getText()));
            int updated = statement.executeUpdate();
            
            if (updated > 0) {
                mainController.updateCustomerTable();
                System.out.println("Customer update successful.");
            }
        } catch (SQLException ex) {
            System.out.println("SQL Error");
            ex.printStackTrace();
        }       
    }
    
    private void clearAppointmentFields() {
        apptIDLabel.setText("");
        apptTitleField.setText("");
        apptTypeCombo.setValue(null);
        apptTypeCombo.setValue(null);
        apptDatePicker.setValue(LocalDate.now());
        apptStartCombo.setValue(null);
        apptEndCombo.setValue(null);
    }
    
    private void disableAppointmentFields() {
        apptIDLabel.setDisable(true);
        apptTitleField.setDisable(true);
        apptTypeCombo.setDisable(true);
        apptDatePicker.setDisable(true);
        apptStartCombo.setDisable(true);
        apptEndCombo.setDisable(true);        
    }
    
    private void enableAppointmentFields() {
        apptIDLabel.setDisable(false);
        apptTitleField.setDisable(false);
        apptTypeCombo.setDisable(false);
        apptDatePicker.setDisable(false);
        apptStartCombo.setDisable(false);
        apptEndCombo.setDisable(false);        
    }
    
    // Accepts local date/time values and returns UTC adjusted Timestamp for insertion to the database.
    public static Timestamp convertToUTC(LocalDate date, String time) {
        ZoneId timeZone = ZoneId.systemDefault();
        LocalTime localTime = LocalTime.parse(time, Session.TIME_FORMAT);
        
        LocalDateTime localDateTime = LocalDateTime.of(date, localTime);
        ZonedDateTime zonedDateTime = localDateTime.atZone(timeZone);
        ZonedDateTime utcDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        LocalDateTime convertedDateTime = utcDateTime.toLocalDateTime();        
        
        return Timestamp.valueOf(convertedDateTime);        
    }
    
    // Acceptes UTC Timestamp from database and returns adjusted date/time based on system time zone. 
    public static LocalDateTime convertToLocal(Timestamp timestamp) {
        ZoneId timeZone = ZoneId.systemDefault();
        ZonedDateTime utcDateTime = timestamp.toLocalDateTime().atZone(ZoneId.of("UTC"));
        ZonedDateTime zonedDateTime = utcDateTime.withZoneSameInstant(timeZone);
        LocalDateTime convertedDateTime = zonedDateTime.toLocalDateTime();
        
        return convertedDateTime;        
    }
    
    private void addAppointment() {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO appointment (customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy)"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)", Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, customerIDLabel.getText());
            statement.setString(2, Integer.toString(Session.getInstance().getUser().getUserID()));
            statement.setString(3, apptTitleField.getText());
            statement.setString(4, apptTypeCombo.getValue());
            statement.setString(5, "");
            statement.setString(6, Session.getInstance().getUser().getUsername()); 
            statement.setString(7, apptTypeCombo.getValue());
            statement.setString(8, "");
            statement.setTimestamp(9, convertToUTC(apptDatePicker.getValue(), apptStartCombo.getValue()));
            statement.setTimestamp(10, convertToUTC(apptDatePicker.getValue(), apptEndCombo.getValue()));
            statement.setString(11, Session.getInstance().getUser().getUsername());
            statement.setString(12, Session.getInstance().getUser().getUsername());
            statement.execute();
            
            ResultSet results = statement.getGeneratedKeys();
            
            if (results.next()) {
                int appointmentID = results.getInt(1);
                System.out.println("Appointment Added Successfully!");
                System.out.println("Appointment ID: " + appointmentID);
            
                mainController.updateAppointmentTable();
                apptIDLabel.setText(Integer.toString(appointmentID));
                updateAppointmentTable();
            }
        } catch (SQLException ex) {
            System.out.println("SQL Error");
            ex.printStackTrace();
        }
    }
    
    private void modifyAppointment() {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE appointment "
                + "SET title = ?, description = ?, contact = ?, type = ?, start = ?, end = ?, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ? "
                + "WHERE appointmentId = ?");
            
            statement.setString(1, apptTitleField.getText());
            statement.setString(2, apptTypeCombo.getValue());
            statement.setString(3, Session.getInstance().getUser().getUsername());
            statement.setString(4, apptTypeCombo.getValue());
            statement.setTimestamp(5, convertToUTC(apptDatePicker.getValue(), apptStartCombo.getValue()));
            statement.setTimestamp(6, convertToUTC(apptDatePicker.getValue(), apptEndCombo.getValue()));
            statement.setString(7, Session.getInstance().getUser().getUsername());
            statement.setString(8, apptIDLabel.getText());
            int updated = statement.executeUpdate();
            
            if (updated > 0) {
                mainController.updateAppointmentTable();
                updateAppointmentTable();
                System.out.println("Appointment update successful.");
            }
        } catch (SQLException ex) {
            System.out.println("SQL Error");
            ex.printStackTrace();
        }  
    }
    
    @FXML
    void deleteAppointment(ActionEvent event) {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();

        if (appointmentTable.getItems().size() < 1) {
            // The customer table is empty.            
        } else if (selectedAppointment == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Appointment Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an appointment to delete from the appointment table.");
            alert.showAndWait();            
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Appointment");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this appointment?\n\n"
                    + "Appointment ID:  " + selectedAppointment.getAppointmentID() + "\n"
                    + "Appointment Title:  " + selectedAppointment.getTitle());
            
            /**
             * Lambda expression utilized here to streamline the code and make it easier to read. 
             */
            alert.showAndWait().ifPresent((result -> {
                if (result == ButtonType.OK) {
                    try {            
                        PreparedStatement statement = connection.prepareStatement("DELETE appointment.* FROM appointment WHERE appointmentId = ?");

                        statement.setInt(1, selectedAppointment.getAppointmentID());
                        int updated = statement.executeUpdate();
                    
                        if (updated > 0) {
                            System.out.println("Appointment " + selectedAppointment.getAppointmentID() + " successfully deleted.");
                            mainController.updateAppointmentTable();                        
                            updateAppointmentTable();
                            selectFirstAppointment();
                        }                     
                    } catch (SQLException ex) {
                     System.out.println("SQL Error");
                        ex.printStackTrace();
                    }
                }
            }));
        }
    }
    
    private void selectFirstAppointment() {
        if (appointmentTable.getItems().size() < 1) {
            clearAppointmentFields();
            disableAppointmentFields();
        } else {
            appointmentTable.getSelectionModel().clearSelection(); // We don't want repeated selections
            appointmentTable.requestFocus();                       // Get the focus
            appointmentTable.getSelectionModel().selectFirst();    // select first item in TableView model
            appointmentTable.getFocusModel().focus(0);             // set the focus on the first element
        }
    }

    private void fadeout(Label label) {
        label.setOpacity(1);
                    
        fade.setNode(label);
        fade.setDelay(Duration.seconds(2));
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setCycleCount(1);
        fade.setAutoReverse(false);
        fade.play();
    }
    
    private boolean validateCustomer() {
        boolean errors = false;
        
        if (nameField.getText().isEmpty()) {
            nameErrorText.setText("\u2022 Please enter a customer name");
            errors = true;
        }
        if (addressField1.getText().isEmpty()) {
            addressErrorText.setText("\u2022 Please enter an address");
            errors = true;
        }
        if (cityField.getText().isEmpty()) {
            cityErrorText.setText("\u2022 Please enter a city");
            errors = true;
        }
        if (countryCombo.getValue() == null) {
            countryErrorText.setText("\u2022 Please select a country");
            errors = true;
        }
        if (zipField.getText().isEmpty()) {
            zipErrorText.setText("\u2022 Please enter a postal code");
            errors = true;
        }
        if (phoneField.getText().isEmpty()) {
            phoneErrorText.setText("\u2022 Please enter a phone number");
            errors = true;
        }
        return errors;
    }
        
    private boolean validateAppointment() {
        boolean valid = true;
        LocalDate date = LocalDate.now();
        
        if (apptTitleField.getText().isEmpty()) {
            titleErrorText.setText("\u2022 Please enter an appointment title");
            valid = false;
        }
        
        if (apptTypeCombo.getValue() == null) {
            typeErrorText.setText("\u2022 Please select an appointment type");
            valid = false;
        }
        
        /**
         * Date should NEVER be null because the field is auto-populated.
         * As another form of Exception Control, this field will throw an IllegalArgumentException if empty.
         * NOTE: I'm not a fan of using Exceptions like, so only using it ONCE.
         */
        if (apptDatePicker.getValue() == null) {
            dateErrorText.setText("\u2022 Please select an appointment date");
            valid = false;
            throw new IllegalArgumentException("No appointment date entered.");
        }
        
        if (apptStartCombo.getValue() == null && apptEndCombo.getValue() == null) {
            durationErrorText.setText("\u2022 Please select a start and end time");
            valid = false;
        } else if (apptStartCombo.getValue() == null) {
            durationErrorText.setText("\u2022 Please select a start time");
            valid = false;
        } else if (apptEndCombo.getValue() == null) {
            durationErrorText.setText("\u2022 Please select an end time");
            valid = false;
        } else if (convertToUTC(date, apptStartCombo.getValue()).after(convertToUTC(date, apptEndCombo.getValue()))) {
            durationErrorText.setText("\u2022 Start must occur before end time.");
            valid = false;
        } else if (apptStartCombo.getValue().equals(apptEndCombo.getValue())) {
            durationErrorText.setText("\u2022 Start and end times are the same.");
            valid = false;
        }
        return valid;
    }
    
    /**
     * Compares proposed appointment start and end times with existing appointments
     * (excluding itself) to determine if new or modified appointment should be accepted.
     * NOTE: To fulfill requirements, this method employs the Try/Catch blocks as  
     * a form of Exception Control.
     * @param startTime
     * @param endTime
     * @param appointmentID
     * @return 
     */    
    private boolean appointmentConflicts(Timestamp startTime, Timestamp endTime, int appointmentID) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM appointment "
                + "WHERE (? < end AND start < ?) AND (userId = ? OR customerId = ?) AND appointmentID != ?");
            
            statement.setTimestamp(1, startTime);
            statement.setTimestamp(2, endTime);
            statement.setInt(3, Session.getInstance().getUser().getUserID());
            statement.setInt(4, Integer.parseInt(customerIDLabel.getText()));
            statement.setInt(5, appointmentID);
            ResultSet results = statement.executeQuery();
            
            results.next();
            int conflicts = results.getInt(1);
            
            if (conflicts > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Appointment Conflict");
                alert.setHeaderText(null);
                alert.setContentText("The proposed appointment overlap with " + conflicts + " existing appointment(s). Please select different start and/or end times.\n\n"
                    + "Start Time: " + apptStartCombo.getValue() + "\n"
                    + "End Time: " + apptEndCombo.getValue());
                alert.showAndWait();    
                               
                return true;
            }            
        } catch (SQLException sqe) {
            System.out.println("SQL Error");
            sqe.printStackTrace();
        } 
        return false;
    }

    private int getCityID() {
        int cityID = 0;
        try {            
            PreparedStatement statement = connection.prepareStatement("SELECT cityId FROM city WHERE city = ? AND countryId = ?");
            
            statement.setString(1, cityField.getText());
            statement.setInt(2, getCountryID());
            ResultSet results = statement.executeQuery();

            if (!results.next()) { // City does NOT exist. Add to database and get primary key.
                statement = connection.prepareStatement("INSERT INTO city (city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy)"
                        + "VALUES (?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)", Statement.RETURN_GENERATED_KEYS);               
                
                statement.setString(1, cityField.getText());
                statement.setInt(2, getCountryID());
                statement.setString(3, Session.getInstance().getUser().getUsername());
                statement.setString(4, Session.getInstance().getUser().getUsername());
                statement.execute();
                
                ResultSet keyResults = statement.getGeneratedKeys();
                
                if (keyResults.next()) {
                    cityID = keyResults.getInt(1);
                    System.out.println("New City: " + cityField.getText());
                    System.out.println("New City ID: " + cityID);
                }
            } else { // City exists. Get primary key.
                cityID = results.getInt("cityId");
                System.out.println("City Found: " + cityField.getText());
                System.out.println("City ID: " + cityID);
            }
        } catch (SQLException ex) {
            System.out.println("SQL Error");
            ex.printStackTrace();
        }
        return cityID;        
    }
    
    private int getCountryID() {
        int countryID = 0;
        String country = countryCombo.getValue();
        for (int x = 0; x < countries.size(); x++) {
            if (countries.get(x).equals(country)) {
                countryID = x + 1;
            }            
        }
        return countryID;
    }

    private void populateCountryCombo() {
        try {
            Statement statement = connection.createStatement();        
            ResultSet results = statement.executeQuery("SELECT * FROM country");

            while (results.next()) {
                countries.add(results.getString("country"));
            }
            countryCombo.setItems(countries);
        } catch (SQLException ex) {
            System.out.println("SQL Error");
            ex.printStackTrace();
        }        
    }
    
    // Populates appointment type list    
    private void populateTypeCombo() {
        ObservableList<String> appointmentType = FXCollections.observableArrayList();
        appointmentType.addAll("First Meeting", "Stock Market", "Tax Preparation", "Retirement Plan");
        apptTypeCombo.setItems(appointmentType);        
    }    
    /**
     * Business hours are assumed to be 8AM - 5PM each day (7 days per week).
     * This method populates the start and end time combo fields so only 
     * appropriate times can be chosen within the user's time zone. 
     */
    private void populateTimeCombos() {
        ObservableList<String> startTimes = FXCollections.observableArrayList();
        ObservableList<String> endTimes = FXCollections.observableArrayList();
	
        ZoneId zone = ZoneId.systemDefault();
        LocalTime time = LocalTime.of(8, 0);
        do {
            startTimes.add(time.format(Session.TIME_FORMAT));          
            endTimes.add(time.format(Session.TIME_FORMAT));
            time = time.plusMinutes(15);
	} while (!time.equals(LocalTime.of(17, 15)));
	
        startTimes.remove(startTimes.size() - 1);
        endTimes.remove(0);

        apptStartCombo.setItems(startTimes);
        apptEndCombo.setItems(endTimes);
        timeZoneLabel.setText(zone.toString());
    }
    
    private void enableCustomerSave() {
        saveCustBtn.setDisable(false);
        saveCloseCustBtn.setDisable(false);
    }
    
    private void enableAppointmentSave() {
        saveApptBtn.setDisable(false);
    }

    /**
     * Initializes record.fxml when adding new appointments or modifying existing customer information.
     * @param customer 
     */
    public void loadCustomerTab(Customer customer) {
        customerIDLabel.setText(Integer.toString(customer.getCustomerID()));
        nameField.setText(customer.getName());
        addressField1.setText(customer.getAddress());
        addressField2.setText(customer.getAddress2());
        cityField.setText(customer.getCity());
        countryCombo.setValue(customer.getCountry());
        zipField.setText(customer.getZip());
        phoneField.setText(customer.getPhone());
             
        saveCustBtn.setDisable(true);
        saveCloseCustBtn.setDisable(true);
        initializeAppointmentTab(customer.getName());
    }
    
    public void initializeAppointmentTab(String customerName) {        
        appointmentsTab.setDisable(false);
        customerNameLabel.setText(customerName);
        apptDatePicker.setValue(LocalDate.now());
        populateTypeCombo();
        populateTimeCombos();
        updateAppointmentTable();
        saveApptBtn.setDisable(true);
    }
    
    public void cancelNewEntry(Appointment appointment) {
        if (apptIDLabel.getText().equalsIgnoreCase("Auto Generated")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cancel New Appointment");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to cancel this entry?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                loadAppointment(appointment);
            } else {
                return; // User chose CANCEL or closed the dialog
            }                    
        }
        loadAppointment(appointment);       
    }
    
    public void loadAppointment(Appointment appointment) {
        LocalDate apptDate = LocalDateTime.parse(appointment.getStartDateTime(), Session.DATE_TIME).toLocalDate();
        LocalTime startTime = LocalDateTime.parse(appointment.getStartDateTime(), Session.DATE_TIME).toLocalTime();
        LocalTime endTime = LocalDateTime.parse(appointment.getEndDateTime(), Session.DATE_TIME).toLocalTime();
        
        apptIDLabel.setText(Integer.toString(appointment.getAppointmentID()));
        apptTitleField.setText(appointment.getTitle());
        apptTypeCombo.setValue(appointment.getType());
        apptDatePicker.setValue(apptDate);
        apptStartCombo.setValue(startTime.format(Session.TIME_FORMAT));
        apptEndCombo.setValue(endTime.format(Session.TIME_FORMAT));
        saveApptBtn.setDisable(true);        
    }
    
    public void loadAppointment() {
        if (appointmentTable.getItems().size() < 1) {
            disableAppointmentFields();
        } else {
            appointmentTable.requestFocus();
            appointmentTable.getSelectionModel().selectFirst();
            appointmentTable.getFocusModel().focus(0);
            Appointment selectedAppt = appointmentTable.getSelectionModel().getSelectedItem();   
            
            LocalDate apptDate = LocalDateTime.parse(selectedAppt.getStartDateTime(), Session.DATE_TIME).toLocalDate();
            LocalTime startTime = LocalDateTime.parse(selectedAppt.getStartDateTime(), Session.DATE_TIME).toLocalTime();
            LocalTime endTime = LocalDateTime.parse(selectedAppt.getEndDateTime(), Session.DATE_TIME).toLocalTime();
        
            apptIDLabel.setText(Integer.toString(selectedAppt.getAppointmentID()));
            apptTitleField.setText(selectedAppt.getTitle());
            apptTypeCombo.setValue(selectedAppt.getType());
            apptDatePicker.setValue(apptDate);
            apptStartCombo.setValue(startTime.format(Session.TIME_FORMAT));
            apptEndCombo.setValue(endTime.format(Session.TIME_FORMAT));
            saveApptBtn.setDisable(true);
        }
    }
    
    
    public void selectTab(int index) {
        recordTabs.getSelectionModel().select(index);
    }
    
    public void updateAppointmentTable() {
        custAppointments.clear();
        
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT appointmentId, customer.customerId, customer.customerName, title, type, userName, start, end "
                + "FROM customer, appointment, user "
                + "WHERE customer.customerId = ? AND customer.customerId = appointment.customerId AND appointment.userId = user.userId");
            
            statement.setString(1, customerIDLabel.getText());
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                int apptID = results.getInt("appointmentId");
                int custID = results.getInt("customerId");
                String custName = results.getString("customerName");
                String title = results.getString("title");
                String type = results.getString("type");
                String consultant = results.getString("userName");
                Timestamp startTS = results.getTimestamp("start");
                Timestamp endTS = results.getTimestamp("end");
                
                String start = convertToLocal(startTS).format(Session.DATE_TIME);
                String end = convertToLocal(endTS).format(Session.DATE_TIME);                

                Appointment appointment = new Appointment(apptID, custID, custName, title, type, consultant, start, end);
                custAppointments.add(appointment);
            }
            appointmentTable.setItems(custAppointments);
        } catch (SQLException ex) {
            System.out.println("SQL Error");
            ex.printStackTrace();
        }        
    }
   
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateCountryCombo();
        
        apptIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        apptTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptStartColumn.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        apptEndColumn.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        apptConsultantColumn.setCellValueFactory(new PropertyValueFactory<>("consultant"));
        
        /**
         * Lambda expressions used for all EventListeners to streamline the code
         * and make it easier to read.
         */
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            nameErrorText.setText("");
            enableCustomerSave();       
            if (newValue.length() > 45) {
                nameField.setText(oldValue);
            }
        });
        
        addressField1.textProperty().addListener((observable, oldValue, newValue) -> {
            addressErrorText.setText("");
            enableCustomerSave();
            if (newValue.length() > 50) {
                addressField1.setText(oldValue);
            }
        });
                
        addressField2.textProperty().addListener((observable, oldValue, newValue) -> {
            enableCustomerSave();
            if (newValue.length() > 50) {
                addressField2.setText(oldValue);
            }
        });

        cityField.textProperty().addListener((observable, oldValue, newValue) -> {
            cityErrorText.setText("");
            enableCustomerSave();
            if (newValue.length() > 50) {
                cityField.setText(oldValue);
            }
        });
        
        countryCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            countryErrorText.setText("");
            enableCustomerSave();
        });

        zipField.textProperty().addListener((observable, oldValue, newValue) -> {
            zipErrorText.setText("");
            enableCustomerSave();
            if (newValue.length() > 10) {
                zipField.setText(oldValue);
            }
        });         
        
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            phoneErrorText.setText("");
            enableCustomerSave();
            if (!newValue.matches("\\d{0,10}")) {
                phoneField.setText(oldValue);
            }
        });        
        apptTitleField.textProperty().addListener((observable, oldValue, newValue) -> {
            titleErrorText.setText("");
            enableAppointmentSave();
            if (newValue.length() > 255) {
                apptTitleField.setText(oldValue);
            }
        });
        
        apptTypeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            typeErrorText.setText("");
            enableAppointmentSave();
        });
        
        apptDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            dateErrorText.setText("");
            enableAppointmentSave();
        });
        
        apptStartCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            durationErrorText.setText("");
            enableAppointmentSave();
        });
        
        apptEndCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            durationErrorText.setText("");
            enableAppointmentSave();
        });
        
        appointmentTable.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cancelNewEntry(newSelection);
            }
        });
    }    
}
