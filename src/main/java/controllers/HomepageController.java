package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML; // import tag
import javafx.scene.text.Text;

public class HomepageController {

    @FXML
    private Text actiontarget;

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        actiontarget.setText("Sign in button pressed");
    }

}
