package ch.makery.address.view;


import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Objects;

import ch.makery.address.MainApp;
import ch.makery.address.model.Player;

public class PlayerOverviewController {

    @FXML
    private TableView<Player> playerTable;
    @FXML
    private TableColumn<Player, String> firstNameColumn;
    @FXML
    private TableColumn<Player, String> lastNameColumn;
    @FXML
    private TableColumn<Player, Integer> recordColumn;
    
    @FXML
    private TableView<MapEntry<String, Integer>> tableScores;
    @FXML
    private TableColumn<MapEntry<String, Integer>, String> dateColumn;
    @FXML
    private TableColumn<MapEntry<String, Integer>, Integer> scoreColumn;
    
   
    

    private MainApp mainApp;
    ObservableMap<String, Integer> map = FXCollections.observableHashMap();
    ObservableList<MapEntry<String, Integer>> entries = FXCollections.observableArrayList();
    
    
    public final class MapEntry<K, V> {

        private final K key;
        private final V value;

        public MapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            // check equality only based on keys
            if (obj instanceof MapEntry) {
                MapEntry<?, ?> other = (MapEntry<?, ?>) obj;
                return Objects.equals(key, other.key);
            } else {
                return false;
            }
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

    }
    
    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public PlayerOverviewController() {
    }
    
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize the person table with the two columns.
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        recordColumn.setCellValueFactory(cellData -> cellData.getValue().recordProperty().asObject());


        
        showPlayerDetails(null);

        // Listen for selection changes and show the person details when changed.
        playerTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPlayerDetails(newValue));
        
        map.addListener((MapChangeListener.Change<? extends String, ? extends Integer> change) -> {
            boolean removed = change.wasRemoved();
            if (removed != change.wasAdded()) {
                if (removed) {
                    // no put for existing key
                    // remove pair completely
                    entries.remove(new MapEntry<>(change.getKey(), (Integer) null));
                } else {
                    // add new entry
                    entries.add(new MapEntry<>(change.getKey(), change.getValueAdded()));
                }
            } else {
                // replace existing entry
                MapEntry<String, Integer> entry = new MapEntry<>(change.getKey(), change.getValueAdded());

                int index = entries.indexOf(entry);
                entries.set(index, entry);
            }
        });
    }
    
    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        playerTable.setItems(mainApp.getPlayerData());
    }
    
    
    /**
     * Fills all text fields to show details about the person.
     * If the specified person is null, all text fields are cleared.
     *
     * @param person the person or null
     */
    private void showPlayerDetails(Player player) {
        if (player != null) {
        	//map = FXCollections.observableMap(player.getScores());

        	map.clear();
        	map.putAll(player.getScores());       	
        	
        	tableScores.setItems(entries);
        	//tableScores = new TableView<>(entries);
        	//dateColumn = new TableColumn<>("Key");
        	//scoreColumn = new TableColumn<>("Value");          	
        	dateColumn.setCellValueFactory(cd -> Bindings.createStringBinding(() -> cd.getValue().getKey()));
        	scoreColumn.setCellValueFactory(cd -> (new SimpleIntegerProperty(cd.getValue().getValue())).asObject());
        	    	
        	tableScores.getColumns().setAll(dateColumn, scoreColumn);
        	
        } else {
        	
        }
    }
    
    
    /**
     * Called when the user clicks on the delete button.
     * @throws ParseException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    @FXML
    private void handleDeletePlayer() throws FileNotFoundException, IOException, ParseException {
        int selectedIndex = playerTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
        	Player p = playerTable.getItems().get(selectedIndex);
        	deleteJSON(p.getFirstName(), p.getLastName());
        	playerTable.getItems().remove(selectedIndex);
           
            //System.out.println(p.getFirstName() + " "+ p.getLastName());
        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Player Selected");
            alert.setContentText("Please select a person in the table.");

            alert.showAndWait();
        }
    }
    
    
    private void deleteJSON(String prenom, String nom) throws FileNotFoundException, IOException, ParseException {
    	JSONParser parser = new JSONParser();
		JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("/Users/tristanmoers/Basket/test.json"));
		JSONArray list = jsonArray;
		for(int i=0; i < jsonArray.size(); i++) {
			JSONObject player = (JSONObject) jsonArray.get(i);
			String prenomdel = (String) player.get("prenom");
			String nomdel = (String) player.get("nom");	
			if(prenomdel.equals(prenom) && nomdel.equals(nom)) {
				list.remove(i);
				break;
			}			
		}
		FileWriter file = new FileWriter("/Users/tristanmoers/Basket/test.json");
		file.write(list.toString());
		file.flush();
    }
    
    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new person.
     * @throws ParseException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    @FXML
    private void handleNewPerson() throws FileNotFoundException, IOException, ParseException {
        Player tempPerson = new Player();
        boolean okClicked = mainApp.showPlayerEditDialog(tempPerson);
        if (okClicked) {
            mainApp.getPlayerData().add(tempPerson);
            addPlayer(tempPerson);
        }
    }
    
    private void addPlayer(Player p) throws FileNotFoundException, IOException, ParseException {
    	JSONParser parser = new JSONParser();
		JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("/Users/tristanmoers/Basket/test.json"));
		JSONArray list = null;
		
		JSONObject o = new JSONObject();
		o.put("prenom", p.getFirstName());
		o.put("nom", p.getLastName());
		o.put("record", 0);
		o.put("scores", list);
		jsonArray.add(o);
		FileWriter file = new FileWriter("/Users/tristanmoers/Basket/test.json");
		file.write(jsonArray.toString());
		file.flush();
    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected person.
     * @throws ParseException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    @FXML
    private void handleEditPerson() throws FileNotFoundException, IOException, ParseException {
        Player selectedPerson = playerTable.getSelectionModel().getSelectedItem();
        String nom = selectedPerson.getLastName();
        String prenom = selectedPerson.getFirstName();
        Map<String, Integer> scores = selectedPerson.getScores();
        if (selectedPerson != null) {
            boolean okClicked = mainApp.showPlayerEditDialog(selectedPerson);
            if (okClicked) {
                showPlayerDetails(selectedPerson);
                editPlayer(selectedPerson, prenom, nom, scores);
            }

        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Player Selected");
            alert.setContentText("Please select a player in the table.");

            alert.showAndWait();
        }
    }
    
    private void editPlayer(Player p, String prenom, String nom,  Map<String, Integer> scores) throws FileNotFoundException, IOException, ParseException {
    	JSONParser parser = new JSONParser();
		JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("/Users/tristanmoers/Basket/test.json"));
		JSONArray list = new JSONArray();
		JSONObject o = new JSONObject();
		o.put("prenom", p.getFirstName());
		o.put("nom", p.getLastName());
		o.put("record", p.getRecord());
		for(String key : scores.keySet()) {
			JSONObject sc = new JSONObject();
			sc.put("date", key);
			sc.put("score", scores.get(key));
			list.add(sc);
		}
		o.put("scores", list);
		
		for(int i=0; i < jsonArray.size(); i++) {
			JSONObject player = (JSONObject) jsonArray.get(i);
			String prenomdel = (String) player.get("prenom");
			String nomdel = (String) player.get("nom");	
			if(prenomdel.equals(prenom) && nomdel.equals(nom)) {
				jsonArray.set(i, o);
				break;
			}			
		}
		FileWriter file = new FileWriter("/Users/tristanmoers/Basket/test.json");
		file.write(jsonArray.toString());
		file.flush();
    }
    
}
