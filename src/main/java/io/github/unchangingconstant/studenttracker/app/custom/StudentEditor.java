package io.github.unchangingconstant.studenttracker.app.custom;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

public class StudentEditor extends HBox implements Controller {
    
    @FXML
    private FormField fullLegalNameField;
    @FXML 
    private FormField prefNameField;
    @FXML 
    private ComboBox<Integer> subjectsField;
    @FXML
    private Button saveButton;

    public StringProperty fullLegalNameTextProperty() {return fullLegalNameField.textProperty();}
    public StringProperty prefNameTextProperty() {return prefNameField.textProperty();}
    public ReadOnlyObjectProperty<Integer> subjectsProperty() {return subjectsField.getSelectionModel().selectedItemProperty();}

    public StudentEditor()  {
        CustomComponentUtils.hookIntoFXML(this, "/view/components/student_editor.fxml");
    }

    @Override
    public void initialize() {
        subjectsField.getItems().addAll(1, 2);
        subjectsField.getSelectionModel().select(0);
    }

    public void setOnAction(EventHandler<ActionEvent> eventHandler)   {
        saveButton.setOnAction(eventHandler);
    }

}
