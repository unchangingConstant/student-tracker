package io.github.unchangingconstant.studenttracker.app.controllers.components;

import java.util.List;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.controllers.ComponentUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

public class ExportDialog extends DialogPane implements Controller {
    
    @FXML
    private Label message;
    @FXML
    private Button button;

    public ExportDialog() {
        super();
        ComponentUtils.hookIntoFXML(this, "/view/components/export_dialog.fxml");
    }

    @Override
    public void initialize() {
        getButtonTypes().add(ButtonType.OK);
    }

}
