package ch.makery.address.view;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.parser.ParseException;

import ch.makery.address.model.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

public class ScoreEditDialogController {

	/*@FXML
    private TextField dateField;*/
	@FXML
	private DatePicker dateField;
    @FXML
    private TextField scoreField;
	
    private Stage dialogStage;
    private Player player;
    private boolean okClicked = false;
    private String pattern = "dd-MM-yyyy";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
    
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
    
    public void setScore(Player player, String date, int score) {
        this.player = player;

        String sc = Integer.toString(score);
        //dateField.setText(date);
        dateField.setValue(fromString(date));
        scoreField.setText(sc);
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
        	DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Map<String, Integer> scores = new TreeMap(new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					try {
						Date d1 = dateFormat.parse(o1);
						Date d2 = dateFormat.parse(o2);
						return d1.compareTo(d2);
					} catch (java.text.ParseException e) {
						return 0;
					}
					
				}
              });
            for(String k : player.getScores().keySet()) {
            	scores.put(k, player.getScores().get(k));
            }
        	scores.put(toString(dateField.getValue()), Integer.parseInt(scoreField.getText()));
            player.setScores(scores);
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
        int parse = 0;
        if (toString(dateField.getValue()) == null || toString(dateField.getValue()).length() == 0) {
            errorMessage += "No valid date!\n";
        }
        if (scoreField.getText() == null || scoreField.getText().length() == 0) {
            errorMessage += "No valid score!\n";
        } 
        try {
            parse = Integer.parseInt(scoreField.getText());
        } catch (NumberFormatException e) {
        	errorMessage += "No valid score!\n";
        }
        if(parse < 0)
        	errorMessage += "No valid score!\n";
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
    
    public String toString(LocalDate date) {
        if (date != null) {
            return dateFormatter.format(date);
        } else {
            return "";
        }
    }

    public LocalDate fromString(String string) {
        if (string != null && !string.isEmpty()) {
            return LocalDate.parse(string, dateFormatter);
        } else {
            return null;
        }
    }
    
}
