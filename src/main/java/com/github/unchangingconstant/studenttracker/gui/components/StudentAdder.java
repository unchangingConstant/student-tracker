package com.github.unchangingconstant.studenttracker.gui.components;

import java.util.List;

import com.github.unchangingconstant.studenttracker.gui.ComponentUtils;
import com.github.unchangingconstant.studenttracker.gui.Controller;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

public class StudentAdder extends HBox implements Controller {
    
    @FXML
    private FormField fullNameField;
    @FXML 
    private FormField prefNameField;
    @FXML 
    private ComboBox<Integer> visitTimeField;
    @FXML
    private Button saveButton;
    @FXML
    private Button displayFormButton;
    @FXML
    private HBox addStudentForm;

    private final SimpleBooleanProperty addingEnabled = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty addingEnabledProperty() {return addingEnabled;}

    public StringProperty fullNameProperty() {return fullNameField.textProperty();}
    public StringProperty prefNameProperty() {return prefNameField.textProperty();}
    public ReadOnlyObjectProperty<Integer> visitTimeProperty() {return visitTimeField.getSelectionModel().selectedItemProperty();}

    public void setOnSaveButtonAction(EventHandler<ActionEvent> handler) {saveButton.setOnAction(handler);};

    public StudentAdder()  {
        super();
        ComponentUtils.hookIntoFXML(this, "/view/components/student_adder.fxml");
    }

    @Override
    public void initialize() {
        getChildren().remove(addStudentForm);
        visitTimeField.getItems().addAll(30, 60);
        visitTimeField.getSelectionModel().select(0);

        displayFormButton.setOnAction(evt -> addingEnabled.set(true));

        addingEnabled.addListener((obs, oldVal, enabled) -> {
            List<Node> children = getChildren();
            if (enabled) {
                children.add(addStudentForm);
                children.remove(displayFormButton);
            } else {
                children.add(displayFormButton);
                children.remove(addStudentForm);
                clear();
            }
        });
    }

    private void clear() {
        fullNameProperty().set("");
        prefNameProperty().set("");
        visitTimeField.getSelectionModel().select(0);
    }

}
