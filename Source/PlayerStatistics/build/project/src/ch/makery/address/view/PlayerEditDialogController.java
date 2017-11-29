package ch.makery.address.view;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ch.makery.address.model.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PlayerEditDialogController {

	@FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
	
    private Stage dialogStage;
    private Player player;
    private boolean okClicked = false;
    
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    }
    
    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    
    /**
     * Sets the person to be edited in the dialog.
     *
     * @param person
     */
    public void setPlayer(Player player) {
        this.player = player;

        firstNameField.setText(player.getFirstName());
        lastNameField.setText(player.getLastName());
    }
    
    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }
    
    
    /**
     * Called when the user clicks ok.
     * @throws ParseException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    @FXML
    private void handleOk() throws FileNotFoundException, IOException, ParseException {
        if (isInputValid()) {
            player.setFirstName(firstNameField.getText());
            player.setLastName(lastNameField.getText());
            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     * @throws ParseException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    private boolean isInputValid() throws FileNotFoundException, IOException, ParseException {
        String errorMessage = "";

        if (firstNameField.getText() == null || firstNameField.getText().length() == 0) {
            errorMessage += "No valid first name!\n";
        }
        if (lastNameField.getText() == null || lastNameField.getText().length() == 0) {
            errorMessage += "No valid last name!\n";
        }
       /* if(isExist(firstNameField.getText(), lastNameField.getText()))
        	errorMessage += "This player already exist!\n";*/
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
    
    private boolean isExist(String prenom, String nom) throws FileNotFoundException, IOException, ParseException {
    	JSONParser parser = new JSONParser();
		JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("test.json"));

		
		for(int i=0; i < jsonArray.size(); i++) {
			JSONObject player = (JSONObject) jsonArray.get(i);
			String prenomdel = (String) player.get("prenom");
			String nomdel = (String) player.get("nom");	
			if(prenomdel.equals(prenom) && nomdel.equals(nom)) {

				return true;
			}			
		}
		return false;
    }
	
}
