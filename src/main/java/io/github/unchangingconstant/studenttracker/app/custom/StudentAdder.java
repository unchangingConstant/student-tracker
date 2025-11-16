package io.github.unchangingconstant.studenttracker.app.custom;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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

    private final SimpleObjectProperty<StudentModel> addedStudent = new SimpleObjectProperty<>(new StudentModel(null, "", "", null, 1));
    public SimpleObjectProperty<StudentModel> addedStudentProperty() {return addedStudent;}

    public StudentAdder()  {
        super();
        CustomComponentUtils.hookIntoFXML(this, "/view/components/student_adder.fxml");
    }

    @Override
    public void initialize() {
        subjectsField.getItems().addAll(1, 2);
        subjectsField.getSelectionModel().select(0);
    }

    public void setOnAddStudent(EventHandler<ActionEvent> eventHandler)   {
        saveButton.setOnAction(eventHandler);
    }

}
