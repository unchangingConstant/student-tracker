package io.github.unchangingconstant.studenttracker.app.controllers.pages;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ExportDialogController implements Controller {
    
    @FXML
    private Label message;
    @FXML
    private Button button;

    @Inject
    public ExportDialogController() {

    }

    @Override
    public void initialize() {
    }

}
