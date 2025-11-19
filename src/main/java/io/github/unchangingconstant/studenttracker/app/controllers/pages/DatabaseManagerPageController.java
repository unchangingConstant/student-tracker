package io.github.unchangingconstant.studenttracker.app.controllers.pages;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.controllers.custom.StudentAdder;
import io.github.unchangingconstant.studenttracker.app.controllers.custom.StudentTableEditor;
import io.github.unchangingconstant.studenttracker.app.models.StudentTableModel;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService.IllegalDatabaseOperationException;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService.InvalidDatabaseEntryException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

/*
 * TODO Think about memory management. When this controller is garbage collected, do the listeners/bindings it adds 
 * to the Singleton StudentTableModel persist? 
 */
public class DatabaseManagerPageController implements Controller {
    
    @FXML 
    private StudentTableEditor studentTable;
    @FXML
    private StudentAdder studentAdder;
    @FXML
    private HBox editorContainer;

    /*
     * MODELS
     */
    // Represents the current editing mode of this page.
    enum EditMode {ADDING_STUDENT, EDITING_STUDENT, EDIT_OFF}
    private SimpleObjectProperty<EditMode> editMode = new SimpleObjectProperty<EditMode>(EditMode.EDIT_OFF);
    // State of student table in the database
    private StudentTableModel studentTableModel;

    private AttendanceService attendanceService;

    @Inject
    public DatabaseManagerPageController(StudentTableModel studentTableModel, AttendanceService attendanceService)  {
        this.attendanceService = attendanceService;
        this.studentTableModel = studentTableModel;
    }

    @Override
    public void initialize() {
        studentTableModel.bindProperty(studentTable.itemsProperty());
        studentTable.setOnDeleteAction(studentId -> onDeleteAction(studentId));
        studentAdder.setOnSaveButtonAction(actionEvent -> onAddStudentAction());
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
        try {
            attendanceService.insertStudent(studentAdder.fullLegalNameProperty().get(), studentAdder.prefNameProperty().get(), studentAdder.subjectsProperty().get());
            studentAdder.addingEnabledProperty().set(false);
        } catch (InvalidDatabaseEntryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onUpdateStudentAction() {
        // TODO
    }
}
