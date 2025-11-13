package io.github.unchangingconstant.studenttracker.app.custom;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

public class StudentEditor extends HBox implements Controller {
    
    @FXML
    private FormField firstNameField;
    @FXML 
    private FormField middleNameField;
    @FXML 
    private FormField lastNameField;
    @FXML 
    private ComboBox<Integer> subjectsField;
    @FXML
    private Button saveButton;

    public StringProperty firstNameTextProperty() {return firstNameField.textProperty();}
    public StringProperty middleNameTextProperty() {return middleNameField.textProperty();}
    public StringProperty lastNameTextProperty() {return lastNameField.textProperty();}


    public StudentEditor()  {
        CustomComponentUtils.hookIntoFXML(this, "/view/components/student_editor.fxml");
    }

    @Override
    public void initialize() {
    }

    public void setOnAction(EventHandler<ActionEvent> eventHandler)   {
        saveButton.setOnAction(eventHandler);
    }

}
