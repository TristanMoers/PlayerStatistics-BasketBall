package ch.makery.address.model;



import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.IntegerProperty;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class Player {

    private StringProperty firstName;
    private StringProperty lastName;
    private IntegerProperty record;
    private Map<String, Integer> scores;
   // private ObservableMap<String,Integer> scores;
    
    /**
     * Default constructor.
     */
    public Player() {
        this(null, null, 0);
    }
    
    /**
     * Constructor with some initial data.
     * 
     */
    public Player(String firstName, String lastName, int record) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.record = new SimpleIntegerProperty(record);
        this.scores = new HashMap<String,Integer>();
        //this.scores = FXCollections.observableMap(map);
    }

    
    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }
    
    public int getRecord() {
        return record.get();
    }

    public void setRecord(int record) {
        this.record.set(record);
    }

    public IntegerProperty recordProperty() {
        return record;
    }
    
    public Map<String, Integer> getScores() {
    	return scores;
    }
    
    public void setScores(Map<String, Integer> scores) {
    	this.scores = scores;
    }
    
}
