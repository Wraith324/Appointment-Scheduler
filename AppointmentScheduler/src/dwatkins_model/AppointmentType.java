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
public class AppointmentType {
    private StringProperty month;
    private StringProperty type;
    private IntegerProperty count;
    
    public AppointmentType() {
        
    }
    
    public AppointmentType(String month, String type, int count) {
        this.month = new SimpleStringProperty(month);
        this.type = new SimpleStringProperty(type);
        this.count = new SimpleIntegerProperty(count);
    }
    
    public void setMonth(String month) {
        this.month.set(month);
    }
    
    public String getMonth() {
        return this.month.get();
    }
    
    public void setType(String type) {
        this.type.set(type);
    }
    
    public String getType() {
        return this.type.get();
    }
    
    public void setCount(int count) {
        this.count.set(count);
    }
    
    public int getCount() {
        return this.count.get();
    }
}