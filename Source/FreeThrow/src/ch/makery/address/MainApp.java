package ch.makery.address;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

import ch.makery.address.model.Player;
import ch.makery.address.view.PlayerEditDialogController;
import ch.makery.address.view.PlayerOverviewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MainApp extends Application {
	
	private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<Player> playerData = FXCollections.observableArrayList();
    
    
    /**
     * Constructor
     * @throws ParseException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public MainApp() throws FileNotFoundException, IOException, ParseException {
    	getPlayerJSON();
    }
    
    public void getPlayerJSON() throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		JSONArray a = (JSONArray) parser.parse(new FileReader("/Users/tristanmoers/Basket/test.json"));
		for(Object o : a) {
			 JSONObject player = (JSONObject) o;
			 String prenom = (String) player.get("prenom");
			 String nom = (String) player.get("nom");
			 int record = (int)(long) player.get("record");
			 JSONArray scores = (JSONArray) player.get("scores");
			 Player p = new Player(prenom, nom, record);
			 HashMap<String, Integer> scoresMap = new HashMap<String, Integer>();
			 for (Object s : scores) {
				 JSONObject sc = (JSONObject) s;
				 String date = (String) sc.get("date");
				 int score = (int)(long) sc.get("score");
				 scoresMap.put(date, score);
			 }
			 p.setScores(scoresMap);
			 playerData.add(p);
		}
    }

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp");
        initRootLayout();
        showPlayerOverview();
	}

	
    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * Shows the person overview inside the root layout.
     */
    public void showPlayerOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PlayerOverview.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(personOverview);
            
            // Give the controller access to the main app.
            PlayerOverviewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
	
    
    /**
     * Returns the data as an observable list of Persons. 
     * @return
     */
    public ObservableList<Player> getPlayerData() {
        return playerData;
    }
	
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Opens a dialog to edit details for the specified person. If the user
	 * clicks OK, the changes are saved into the provided person object and true
	 * is returned.
	 *
	 * @param person the person object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 */
	public boolean showPlayerEditDialog(Player player) {
	    try {
	        // Load the fxml file and create a new stage for the popup dialog.
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/PlayerEditDialog.fxml"));
	        AnchorPane page = (AnchorPane) loader.load();

	        // Create the dialog Stage.
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Edit Player");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);

	        // Set the person into the controller.
	        PlayerEditDialogController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
	        controller.setPlayer(player);

	        // Show the dialog and wait until the user closes it
	        dialogStage.showAndWait();

	        return controller.isOkClicked();
	    } catch (IOException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
}
