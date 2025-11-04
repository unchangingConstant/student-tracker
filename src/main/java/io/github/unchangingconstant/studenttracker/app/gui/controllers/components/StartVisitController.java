package io.github.unchangingconstant.studenttracker.app.gui.controllers.components;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.custom.AutoCompleteTextField;
import io.github.unchangingconstant.studenttracker.app.gui.models.StudentTableModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Auto completer w/ student names and stuff
 */
public class StartVisitController implements Controller {

    private StudentTableModel model;

    @FXML
    private AutoCompleteTextField<Student> startVisit; 
    @FXML
    private Button startVisitButton;

    @Inject
    public StartVisitController(StudentTableModel model)    {
        this.model = model;
    }

    @Override
    public void initialize() {
    }
    
}
