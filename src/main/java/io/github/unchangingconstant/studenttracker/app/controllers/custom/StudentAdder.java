package io.github.unchangingconstant.studenttracker.app.controllers.custom;

import java.util.List;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
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
    private FormField fullLegalNameField;
    @FXML 
    private FormField prefNameField;
    @FXML 
    private ComboBox<Integer> subjectsField;
    @FXML
    private Button saveButton;
    @FXML
    private Button displayFormButton;
    @FXML
    private HBox addStudentForm;

    private SimpleBooleanProperty addingEnabled = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty addingEnabledProperty() {return addingEnabled;}

    public StringProperty fullLegalNameProperty() {return fullLegalNameField.textProperty();}
    public StringProperty prefNameProperty() {return prefNameField.textProperty();}
    public ReadOnlyObjectProperty<Integer> subjectsProperty() {return subjectsField.getSelectionModel().selectedItemProperty();}

    public void setOnSaveButtonAction(EventHandler<ActionEvent> handler) {saveButton.setOnAction(handler);};

    public StudentAdder()  {
        super();
        CustomComponentUtils.hookIntoFXML(this, "/view/components/student_adder.fxml");
    }

    @Override
    public void initialize() {
        getChildren().remove(addStudentForm);
        subjectsField.getItems().addAll(1, 2);
        subjectsField.getSelectionModel().select(0);

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
        fullLegalNameProperty().set("");
        prefNameProperty().set("");
        subjectsField.getSelectionModel().select(0);
    }

}
