/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dwatkins_view;

import dwatkins_model.Appointment;
import dwatkins_model.AppointmentType;
import drwatkins_util.DBConnection;
import dwatkins_model.Session;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author David Watkins
 */
public class FXML_ReportsController implements Initializable {
    
    private final ObservableList<AppointmentType> appointmentTypeList = FXCollections.observableArrayList();
    private ObservableList<String> consultants = FXCollections.observableArrayList();
    private ObservableList<String> months = FXCollections.observableArrayList();
    private static Connection connection = DBConnection.getConnection();
    
    @FXML
    private TableView<AppointmentType> apptTypeTable;
    
    @FXML
    private TableView<Appointment> scheduleTable;
    
    @FXML
    private ComboBox<String> monthCombo, consultantCombo;
    
    @FXML
    private TableColumn<?, String> monthColumn, typeColumn, countColumn;
    
    @FXML
    private TableColumn<Appointment, String> apptDateColumn, apptStartColumn, apptEndColumn, apptTitleColumn, apptTypeColumn, apptCustomerColumn;

    @FXML
    private MenuItem logoutLabel;
    
    @FXML
    private BarChart<String, Integer> customerCountryChart, customerCityChart;

    @FXML
    void filterMonth(ActionEvent event) {
        // Filter by Month.
        FilteredList<AppointmentType> selectedMonth = new FilteredList<>(appointmentTypeList);
        selectedMonth.setPredicate(row -> {
            String rowMonth = row.getMonth();

            return rowMonth.equals(monthCombo.getValue());
        });
        apptTypeTable.setItems(selectedMonth);
    }

    @FXML
    void filterSchedule(ActionEvent event) {
        // Filter by Consultant.
        FilteredList<Appointment> consultantFilter = new FilteredList<>(Session.getInstance().getAppointmentList());
        consultantFilter.setPredicate(row -> {
            String rowConsultant = row.getConsultant();

            return rowConsultant.equals(consultantCombo.getValue());
        });
        // Filter by Consultant.
        FilteredList<Appointment> dateFilter = new FilteredList<>(consultantFilter);
        dateFilter.setPredicate(row -> {
            LocalDate rowDate = LocalDate.parse(row.getDate(), Session.DATE_FORMAT);

            return rowDate.isEqual(LocalDate.now()) || rowDate.isAfter(LocalDate.now());
        });
        scheduleTable.setItems(dateFilter);
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
    void loadRecords(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/dwatkins_view/FXML_Main.fxml"));
        Scene login = new Scene(root);
        
        Stage stage = Session.getInstance().getStage();
        stage.setScene(login);
        stage.setTitle("Appointment Scheduling System");
        stage.setResizable(false);
    }
    
    private void populateAppointmentTypeTable() {
        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(
                "SELECT MONTHNAME(start) AS 'month', type, Count(*) AS 'count' "
                + "FROM appointment "
                + "WHERE MONTH(start) > MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 6 MONTH)) "        
                + "GROUP BY MONTHNAME(start), type "
                + "ORDER BY month asc");

            while (results.next()) {
                String apptMonth = results.getString("month");
                String apptType = results.getString("type");
                int count = results.getInt("count");
                
                AppointmentType appointmentType = new AppointmentType(apptMonth, apptType, count);
                appointmentTypeList.add(appointmentType);
            }
            apptTypeTable.setItems(appointmentTypeList);
        } catch (SQLException ex) {
            System.out.println("SQL Error");
            ex.printStackTrace();
        }        
    }
    
    private void populateScheduleTable() {
        scheduleTable.setItems(Session.getInstance().getAppointmentList());
    }
    
    private void populateCountryBarChart() {
        XYChart.Series<String,Integer> series = new XYChart.Series<>();
        
        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(
                "SELECT country, COUNT(*) "
                + "FROM customer, address, city, country "
                + "WHERE customer.addressId = address.addressId AND address.cityId = city.cityId AND city.countryId = country.countryId "
                + "GROUP BY country");
            
            while (results.next()) {
                String country = results.getString("country");
                int count = results.getInt("COUNT(*)");
                
                series.getData().add(new XYChart.Data<>(country, count));
            }
            customerCountryChart.getData().add(series);
        } catch (SQLException ex) {
            System.out.println("SQL Error");
            ex.printStackTrace();
        }
    }
    
    private void populateCityBarChart() {
        XYChart.Series<String,Integer> series = new XYChart.Series<>();
        
        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(
                "SELECT city, COUNT(*) "
                + "FROM customer, address, city "
                + "WHERE customer.addressId = address.addressId AND address.cityId = city.cityId "
                + "GROUP BY city");
            
            while (results.next()) {
                String city = results.getString("city");
                int count = results.getInt("COUNT(*)");
                
                series.getData().add(new XYChart.Data<>(city, count));
            }
            customerCityChart.getData().add(series);
        } catch (SQLException ex) {
            System.out.println("SQL Error");
            ex.printStackTrace();
        }
    }
    
    private void populateMonthCombo() {
        appointmentTypeList.stream().filter(distinctByKey(p -> p.getMonth())).forEach(p -> months.add(p.getMonth()));
        monthCombo.setItems(months);        
    }
    
    private void populateConsultantCombo() {
        Session.getInstance().getAppointmentList().stream().filter(distinctByKey(p -> p.getConsultant())).forEach(p -> consultants.add(p.getConsultant()));
        consultantCombo.setItems(consultants);
    }
    
    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        monthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        
        apptDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        apptStartColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        apptEndColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        apptTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        
        populateScheduleTable();
        populateAppointmentTypeTable();
        populateMonthCombo();
        populateConsultantCombo();
        populateCountryBarChart();
        populateCityBarChart();
        
        // Set schedule to current user.
        consultantCombo.getSelectionModel().select(Session.getInstance().getUser().getUsername());
        Event.fireEvent(consultantCombo, new ActionEvent());
    }   
}