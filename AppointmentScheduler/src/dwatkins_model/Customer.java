/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dwatkins_model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author David Watkins
 */
public class Customer {
    private IntegerProperty customerID;
    private StringProperty name;
    private StringProperty address;
    private StringProperty address2;
    private StringProperty city;
    private StringProperty country;
    private StringProperty zip;
    private StringProperty phone;
    
    public Customer() {        
    }
    
    public Customer(int customerID, String name, String address, String city, String country, String zip, String phone) {
        this.customerID = new SimpleIntegerProperty(customerID);
        this.name = new SimpleStringProperty(name);
        this.address = new SimpleStringProperty(address);
        this.city = new SimpleStringProperty(city);
        this.country = new SimpleStringProperty(country);
        this.zip = new SimpleStringProperty(zip);
        this.phone = new SimpleStringProperty(phone);
    }
    
    public Customer(int customerID, String name, String address, String address2, String city, String country, String zip, String phone) {
        this.customerID = new SimpleIntegerProperty(customerID);
        this.name = new SimpleStringProperty(name);
        this.address = new SimpleStringProperty(address);
        this.address2 = new SimpleStringProperty(address2);
        this.city = new SimpleStringProperty(city);
        this.country = new SimpleStringProperty(country);
        this.zip = new SimpleStringProperty(zip);
        this.phone = new SimpleStringProperty(phone);
    }
    
    public void setCustomerID(int customerID) {
        this.customerID.set(customerID);
    }
    
    public int getCustomerID() {
        return this.customerID.get();
    }
    
    public void setName(String name) {
        this.name.set(name);
    }
    
    public String getName() {
        return this.name.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }
    
    public String getAddress() {
        return this.address.get();
    }
    
    public void setAddress2(String address) {
        this.address2.set(address);
    }
    
    public String getAddress2() {
        return this.address2.get();
    }
    
    public void setCity(String city) {
        this.city.set(city);
    }
    
    public String getCity() {
        return this.city.get();
    }
    
    public void setCountry(String country) {
        this.country.set(country);
    }
    
    public String getCountry() {
        return this.country.get();
    }
    
    public void setZip(String zip) {
        this.zip.set(zip);
    }
    
    public String getZip() {
        return this.zip.get();
    }
    
    public void setPhone(String phone) {
        this.phone.set(phone);
    }
    
    public String getPhone() {
        return this.phone.get();
    }    
}