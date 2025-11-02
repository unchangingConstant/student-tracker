package io.github.unchangingconstant.studenttracker.app.gui.controllers.components;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.models.StudentTableModel;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

/**
 * Auto completer w/ student names and stuff
 */
public class StartVisitController implements Controller {

    private StudentTableModel model;

    @FXML
    private ComboBox<Student> startVisit; 

    @Inject
    public StartVisitController(StudentTableModel model)    {
        this.model = model;
    }

    @Override
    public void initialize() {
        startVisit.setItems(model.getStudents());
    }
    
}
