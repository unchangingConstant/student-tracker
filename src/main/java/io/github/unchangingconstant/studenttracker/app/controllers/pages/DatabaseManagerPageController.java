package io.github.unchangingconstant.studenttracker.app.controllers.pages;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.custom.StudentAdder;
import io.github.unchangingconstant.studenttracker.app.custom.StudentTableEditor;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import io.github.unchangingconstant.studenttracker.app.models.StudentTableModel;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService.IllegalDatabaseOperationException;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService.InvalidDatabaseEntryException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/*
 * TODO Think about memory management. When this controller is garbage collected, do the listeners/models it adds 
 * to the Singleton StudentTableModel persist? 
 */
public class DatabaseManagerPageController implements Controller {
    
    @FXML 
    private StudentTableEditor studentTable;
    @FXML
    private Button addStudentButton;
    @FXML
    private StudentAdder studentAdder;
    @FXML
    private HBox editorContainer;

    private AttendanceService attendanceService;
    private StudentTableModel studentTableModel;

    @Inject
    public DatabaseManagerPageController(StudentTableModel studentTableModel, AttendanceService attendanceService)  {
        this.attendanceService = attendanceService;
        this.studentTableModel = studentTableModel;
    }

    @Override
    public void initialize() {
        studentTableModel.bindProperty(studentTable.itemsProperty());
        studentTable.setOnDeleteAction((studentId) -> onDeleteAction(studentId));
        studentAdder.setOnAddStudent(actionEvent -> onAddStudentAction());
        // If editingEnabled changes, the displayed component changes to reflect that value.
        // See edittingEnabled property in DatabaseManagerViewModel
        editorContainer.getChildren().remove(studentAdder);
    }

    public void onDeleteAction(Integer studentId) {
        try {
            attendanceService.deleteStudent(studentId);
        } catch (IllegalDatabaseOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onAddStudentAction()  {
        StudentModel student = studentAdder.addedStudentProperty().get();
        try {
            attendanceService.insertStudent(student.getFullLegalName().get(), student.getPrefName().get(), student.getSubjects().get());
        } catch (InvalidDatabaseEntryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
