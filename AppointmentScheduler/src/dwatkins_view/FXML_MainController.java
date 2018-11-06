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
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author David Watkins
 */
public class FXML_MainController implements Initializable {
    
    private static FXML_RecordController recordController;
    private static Connection connection;

    
    @FXML
    private TextField searchCustVal;
    
    @FXML
    private Label userLabel, timeZoneLabel;
    
    @FXML
    private TableView<Customer> customerTable;
    
    @FXML
    private TableView<Appointment> appointmentTable;
    
    @FXML
    private ToggleGroup appointmentDates;

    @FXML
    private TableColumn<Customer, Integer> customerID;

    @FXML
    private TableColumn<Customer, String> customerName, customerPhone, customerAddress, customerCity, customerZip, customerCountry;
    
    @FXML
    private TableColumn<Appointment, String> apptID, apptTitle, apptType, apptCustomer, apptStart, apptEnd, apptConsultant;
       
    @FXML
    private Button  userActivityBtn, clearSearchBtn, newCustBtn, modifyCustBtn, deleteCustBtn, newApptBtn, modifyApptBtn, deleteApptBtn;
        
    @FXML
    private RadioButton radio1, radio2, radio3;
    
    @FXML
    private Tab customerTab;
    
    @FXML
    private Menu reportsMenu;
    
    @FXML
    private MenuItem logoutLabel;
    
    @FXML
    void openUserLog(ActionEvent event) throws IOException {
        File file = new File("userActivity.log");
        Desktop.getDesktop().open(file);    
    }

    @FXML
    void newCustomer(ActionEvent event) throws IOException {
        
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dwatkins_view/FXML_Record.fxml"));
        Parent root = loader.load();
        
        recordController = (FXML_RecordController)loader.getController();
        recordController.setMainController(this);        
        stage.setScene(new Scene(root));
        stage.setTitle("Appointment Scheduling System - Record");
        stage.setResizable(false);
        
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(newCustBtn.getScene().getWindow());
        stage.showAndWait();
    }

    @FXML
    void modifyCustomer(ActionEvent event) throws IOException {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        
        if (customerTable.getItems().size() < 1) {
            return; // The customer table is empty.
        } else if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Customer Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a customer to modify from the customer table.");
            alert.showAndWait();
            return; // No customer selected.
        }
        
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dwatkins_view/FXML_Record.fxml"));
        Parent root = loader.load();
        
        recordController = (FXML_RecordController)loader.getController();
        recordController.setMainController(this);        
        stage.setScene(new Scene(root));
        stage.setTitle("Appointment Scheduling System - Record");
        stage.setResizable(false);
        
        recordController.loadCustomerTab(selectedCustomer);
        recordController.loadAppointment();
        
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(newCustBtn.getScene().getWindow());
        stage.showAndWait();
    }

    @FXML
    void deleteCustomer(ActionEvent event) {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        if (customerTable.getItems().size() < 1) {
            // The customer table is empty.            
        } else if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Customer Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a customer to delete from the customer table.");
            alert.showAndWait();            
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Customer");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this customer?\n\n"
                    + "Customer ID:  " + selectedCustomer.getCustomerID() + "\n"
                    + "Customer Name:  " + selectedCustomer.getName());

            /**
             * Lambda expression utilized here to streamline the code and make it easier to read. 
             */
            alert.showAndWait().ifPresent((result -> {
                if (result == ButtonType.OK) {
                    try {            
                        connection = DBConnection.getConnection();
                        PreparedStatement statement = connection.prepareStatement(""
                            + "DELETE customer.*, address.* "
                            + "FROM customer, address "
                            + "WHERE customer.customerId = ? AND customer.addressId = address.addressId");

                        statement.setInt(1, selectedCustomer.getCustomerID());
                        int updated = statement.executeUpdate();
                    
                        if (updated > 0) {
                            System.out.println("Customer " + selectedCustomer.getCustomerID() + " successfully deleted.");
                            updateCustomerTable();
                        }                     
                    } catch (SQLException ex) {
                        System.out.println("SQL Error");
                        ex.printStackTrace();
                    }
                } 
            }));
        }
    }

    @FXML
    void newAppointment(ActionEvent event) throws IOException {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        
        if (customerTable.getItems().size() < 1) {
            return; // The customer table is empty.
        } else if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Customer Selected");
            alert.setHeaderText(null);
            alert.setContentText("To add an appointment, please first select a customer from the customer table.");
            alert.showAndWait();
            return; // No customer selected.
        }
        
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dwatkins_view/FXML_Record.fxml"));
        Parent root = loader.load();
        
        recordController = (FXML_RecordController)loader.getController();
        recordController.setMainController(this);        
        stage.setScene(new Scene(root));
        stage.setTitle("Appointment Scheduling System - Record");
        stage.setResizable(false);
        
        recordController.loadCustomerTab(selectedCustomer);
        recordController.newAppointment(event);
        recordController.selectTab(1);
                
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(newCustBtn.getScene().getWindow());
        stage.showAndWait();
    }

    @FXML
    void modifyAppointment(ActionEvent event) throws IOException {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        
        if (customerTable.getItems().size() < 1) {
            return; // The appointment table is empty.
        } else if (selectedAppointment == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Appointment Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an appointment to modify from the appointment table.");
            alert.showAndWait();
            return; // No appointment selected.
        }
        
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dwatkins_view/FXML_Record.fxml"));
        Parent root = loader.load();
        
        recordController = (FXML_RecordController)loader.getController();
        recordController.setMainController(this);        
        stage.setScene(new Scene(root));
        stage.setTitle("Appointment Scheduling System - Record");
        stage.setResizable(false);
        
        recordController.loadCustomerTab(Session.getInstance().getCustomer(selectedAppointment.getCustomerID()));
        recordController.loadAppointment(selectedAppointment);
        recordController.selectTab(1);
        
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(newCustBtn.getScene().getWindow());
        stage.showAndWait();
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

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                try {            
                    connection = DBConnection.getConnection();
                    PreparedStatement statement = connection.prepareStatement(""
                        + "DELETE appointment.* "
                        + "FROM appointment "
                        + "WHERE appointmentId = ?");

                    statement.setInt(1, selectedAppointment.getAppointmentID());
                    int updated = statement.executeUpdate();
                    
                    if (updated > 0) {
                        System.out.println("Appointment " + selectedAppointment.getAppointmentID() + " successfully deleted.");
                        updateAppointmentTable();
                    }                     
                } catch (SQLException ex) {
                    System.out.println("SQL Error");
                    ex.printStackTrace();
                }
            } else {
                // User chose CANCEL or closed the dialog
            }
        }
    }
    
    @FXML
    void logout(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/dwatkins_view/FXML_Login.fxml"));
        Scene login = new Scene(root);
        
        Stage stage = Session.getInstance().getStage();
        stage.setScene(login);
        stage.setTitle("Appointment Scheduling System - Login");
        stage.setResizable(false);
    }

    @FXML
    void exitProgram(ActionEvent event) {
        DBConnection.close();
        System.exit(0);
    }
    
    @FXML
    void loadReports(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/dwatkins_view/FXML_Reports.fxml"));
        Scene login = new Scene(root);
        
        Stage stage = Session.getInstance().getStage();
        stage.setScene(login);
        stage.setTitle("Appointment Scheduling System");
        stage.setResizable(false);
    }
    
    @FXML
    void clearSearch(ActionEvent event) {
        clearSearch();
    }
    
    void clearSearch() {
        if (!searchCustVal.getText().isEmpty()) {
            searchCustVal.setText("");
            customerTable.setItems(Session.getInstance().getCustomerList());
        }
    }
    
    void searchCustomers() {
        FilteredList<Customer> filteredCustomers = new FilteredList<>(Session.getInstance().getCustomerList());
        filteredCustomers.setPredicate(customer -> {
            if (searchCustVal.getText() == null || searchCustVal.getText().isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchCustVal.getText().toLowerCase();
                
            if (Integer.toString(customer.getCustomerID()).contains(lowerCaseFilter)) {
                return true; // Filter matches customer ID.
            } else if (customer.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches customer name.
            } else if (customer.getCity().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches customer city.
            } else if (customer.getAddress().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches customer address.
            }
            return false; // No matches found
        });
        
        SortedList<Customer> sortedCustomers = new SortedList<>(filteredCustomers);
        sortedCustomers.comparatorProperty().bind(customerTable.comparatorProperty());
        customerTable.setItems(sortedCustomers);       
    }
    
    @FXML
    void filterByMonth(ActionEvent event) {
        filterByMonth();
    }

    @FXML
    void filterByWeek(ActionEvent event) {
        filterByWeek();
    }
    
    @FXML
    void showAllAppointments(ActionEvent event) {
        appointmentTable.setItems(Session.getInstance().getAppointmentList());
    }
    
    /**
     * Filters appointment table to show user appointments for the upcoming month.
     */
    public void filterByMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlus1Month = now.plusMonths(1);
        // Filter by Date.
        FilteredList<Appointment> filteredByDate = new FilteredList<>(Session.getInstance().getAppointmentList());
        filteredByDate.setPredicate(row -> {
            LocalDateTime rowDate = LocalDateTime.parse(row.getStartDateTime(), Session.DATE_TIME);

            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowPlus1Month);
        });
        // Filter by User.
        FilteredList<Appointment> filteredByUser = new FilteredList<>(filteredByDate);
        filteredByUser.setPredicate(row -> {
            String rowUser = row.getConsultant();

            return rowUser.matches(Session.getInstance().getUser().getUsername());
        });
        appointmentTable.setItems(filteredByUser);        
    }
    
    /**
     * Filters appointment table to show user appointments for the upcoming week.
     */
    public void filterByWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlus7Days = now.plusDays(7);
        // Filter by Date.
        FilteredList<Appointment> filteredByDate = new FilteredList<>(Session.getInstance().getAppointmentList());
        filteredByDate.setPredicate(row -> {
            LocalDateTime rowDate = LocalDateTime.parse(row.getStartDateTime(), Session.DATE_TIME);

            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowPlus7Days);
        });
        // Filter by User.
        FilteredList<Appointment> filteredByUser = new FilteredList<>(filteredByDate);
        filteredByUser.setPredicate(row -> {
            String rowUser = row.getConsultant();

            return rowUser.matches(Session.getInstance().getUser().getUsername());
        });
        appointmentTable.setItems(filteredByUser);        
    }
    
    public void updateAppointmentTable() {
        Session.getInstance().clearAppointmentList();
 
        try {
            connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();        
            ResultSet results = statement.executeQuery(
                "SELECT appointmentId, customer.customerId, customer.customerName, title, type, userName, start, end "
                + "FROM customer, appointment, user "
                + "WHERE customer.customerId = appointment.customerId AND appointment.userId = user.userId");

            while (results.next()) {
                int apptID = results.getInt("appointmentId");
                int custID = results.getInt("customerId");
                String custName = results.getString("customerName");
                String title = results.getString("title");
                String type = results.getString("type");
                String consultant = results.getString("userName");
                Timestamp startTS = results.getTimestamp("start");
                Timestamp endTS = results.getTimestamp("end");
                
                String start = FXML_RecordController.convertToLocal(startTS).format(Session.DATE_TIME);
                String end = FXML_RecordController.convertToLocal(endTS).format(Session.DATE_TIME);
                
                
                Appointment appointment = new Appointment(apptID, custID, custName, title, type, consultant, start, end);
                Session.getInstance().addAppointment(appointment);
            }
            appointmentTable.setItems(Session.getInstance().getAppointmentList());
            
            if (radio1.isSelected()) {
            
            } else if (radio2.isSelected()) {
                filterByWeek();
            } else if (radio3.isSelected()) {
                filterByMonth();
            }
        } catch (SQLException ex) {
            System.out.println("SQL Error");
            ex.printStackTrace();
        }        
    }
    
    public void updateCustomerTable() {
        clearSearch();
        Session.getInstance().clearCustomerList();
       
        try {
            connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();        
            ResultSet results = statement.executeQuery("SELECT customerID, customerName, address, address2, city, country, postalCode, phone "
                + "FROM customer, address, city, country "
                + "WHERE customer.addressID = address.addressID AND address.cityID = city.cityID AND city.countryId = country.countryID");

            while (results.next()) {
                int custID = results.getInt("customerId");
                String custName = results.getString("customerName");
                String custAddress1 = results.getString("address");
                String custAddress2 = results.getString("address2");
                String custCity = results.getString("city");
                String custCountry = results.getString("country");
                String custZip = results.getString("postalCode");
                String custPhone = results.getString("phone");
                
                Customer customer = new Customer(custID, custName, custAddress1, custAddress2, custCity, custCountry, custZip, custPhone);
                Session.getInstance().addCustomer(customer);
            }
            customerTable.setItems(Session.getInstance().getCustomerList());
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
        ZoneId zone = ZoneId.systemDefault();
        logoutLabel.setText("Logout: " + Session.getInstance().getUser().getUsername());
        userLabel.setText(Session.getInstance().getUser().getUsername());
        timeZoneLabel.setText(zone.toString());
        
        customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        customerZip.setCellValueFactory(new PropertyValueFactory<>("zip"));
        customerCountry.setCellValueFactory(new PropertyValueFactory<>("country"));
        
        apptID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        apptTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptType.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        apptConsultant.setCellValueFactory(new PropertyValueFactory<>("consultant"));
        apptStart.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        apptEnd.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        
        updateCustomerTable();
        updateAppointmentTable();
        
        /**
         * Lambda expressions used for the following EventListeners to streamline
         * the code and make it easier to read.
         */        
        searchCustVal.textProperty().addListener((observable, oldValue, newValue) -> {
            searchCustomers();
        });
        appointmentDates.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updateAppointmentTable();
        });       
    }    
}