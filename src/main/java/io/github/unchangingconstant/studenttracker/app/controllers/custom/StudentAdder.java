package io.github.unchangingconstant.studenttracker.app.controllers.custom;

import java.util.List;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
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

    public String getFullLegalNameInput() {return fullLegalNameField.getText();}
    public String getPrefNameInput() {return prefNameField.getText();}
    public Integer getSubjectsInput() {return subjectsField.getSelectionModel().getSelectedItem();}

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

        addingEnabled.addListener((obs, oldVal, newVal) -> {
            List<Node> children = getChildren();
            if (newVal) {
                children.add(addStudentForm);
                children.remove(displayFormButton);
            } else {
                children.add(displayFormButton);
                children.remove(addStudentForm);
            }
        });
    }

}
