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
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

        public final K key;
        public final V value;

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
    private void handleDeleteScore() throws FileNotFoundException, IOException, ParseException {
        int selectedScoreIndex = tableScores.getSelectionModel().getSelectedIndex();
        int selectedPlayerIndex = playerTable.getSelectionModel().getSelectedIndex();
        if (selectedScoreIndex >= 0) {
        	MapEntry<String, Integer> s = tableScores.getItems().get(selectedScoreIndex);
        	Player p = playerTable.getItems().get(selectedPlayerIndex);
        	mainApp.jsonMethod.deleteScore(p.getFirstName(), p.getLastName(), s);
        	tableScores.getItems().remove(selectedScoreIndex);
        	p.getScores().remove(s.key);
        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Score Selected");
            alert.setContentText("Please select a score in the table.");

            alert.showAndWait();
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
        	mainApp.jsonMethod.deletePlayer(p.getFirstName(), p.getLastName());
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
            mainApp.jsonMethod.addPlayer(tempPerson);
        }
    }
    
    
    @FXML
    private void handleAddScore() throws FileNotFoundException, IOException, ParseException {      
        int selectedPlayerIndex = playerTable.getSelectionModel().getSelectedIndex();
        if (selectedPlayerIndex >= 0) {      	
        	Player p = playerTable.getItems().get(selectedPlayerIndex);      
        	boolean okClicked = mainApp.showScoreEditDialog(p, null, 0);
        	mainApp.jsonMethod.addNewScore(p);
        	map.clear();
        	map.putAll(p.getScores());       	
        	
        	tableScores.setItems(entries);
        	tableScores.getColumns().setAll(dateColumn, scoreColumn);
        	
        	p.setRecord(calculRecord(p));
        	mainApp.jsonMethod.SetRecord(p);
        	showPlayerDetails(p);
        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Score Selected");
            alert.setContentText("Please select a score in the table.");

            alert.showAndWait();
        }
    }


    @FXML
    private void handleEditScore() throws FileNotFoundException, IOException, ParseException {      
        int selectedPlayerIndex = playerTable.getSelectionModel().getSelectedIndex();
        int selectedScoreIndex = tableScores.getSelectionModel().getSelectedIndex();
        if (selectedScoreIndex >= 0) {     
        	MapEntry<String, Integer> s = tableScores.getItems().get(selectedScoreIndex);
        	Player p = playerTable.getItems().get(selectedPlayerIndex);      
        	boolean okClicked = mainApp.showScoreEditDialog(p, s.key, s.value);
        	mainApp.jsonMethod.editScore(p, s.key, s.value);
        	map.clear();
        	map.putAll(p.getScores());       	
        	
        	tableScores.setItems(entries);
        	tableScores.getColumns().setAll(dateColumn, scoreColumn);
        	p.setRecord(calculRecord(p));
        	mainApp.jsonMethod.SetRecord(p);
        	showPlayerDetails(p);
        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Score Selected");
            alert.setContentText("Please select a score in the table.");

            alert.showAndWait();
        }
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
                mainApp.jsonMethod.editPlayer(selectedPerson, prenom, nom, scores);
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
    

    
    @FXML
    private void handleViewStat() {
        Player selectedPerson = playerTable.getSelectionModel().getSelectedItem();
        if (selectedPerson != null) { 
        	String nom = selectedPerson.getLastName();
        	String prenom = selectedPerson.getFirstName();
    	 
        	 Stage dialogStage = new Stage();
    	     dialogStage.setTitle("Statistics");
    	     dialogStage.initModality(Modality.WINDOW_MODAL);
    	     dialogStage.initOwner(mainApp.getPrimaryStage());
    	     final CategoryAxis xAxis = new CategoryAxis();
    	     NumberAxis yAxis = new NumberAxis(0, 100, 1);
    	     yAxis.setTickUnit(1.0);
    	     yAxis.setMinorTickVisible(false);
    	     xAxis.setLabel("Training Sessions");
    	     yAxis.setLabel("Scores");
    	     final LineChart<String,Number> lineChart = 
    	                new LineChart<String,Number>(xAxis,yAxis);
    	     lineChart.setTitle(prenom + " " +nom+" statistics");
    	     XYChart.Series series = new XYChart.Series();
    	     for(Entry<String, Integer> e : selectedPerson.getScores().entrySet()) {
    	    	 series.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
    	     }
    	     Scene scene  = new Scene(lineChart,800,600);
    	     lineChart.getData().add(series);
    	     dialogStage.setScene(scene);
    	     dialogStage.show();
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
    
    
    public int calculRecord(Player p) {
    	int record = 0;
    	for(Entry<String, Integer> e: p.getScores().entrySet()) {
    		if(e.getValue() > record)
    			record = e.getValue();
    	}
    	return record;
    }
    
}
