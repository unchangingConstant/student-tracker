package io.github.unchangingconstant.studenttracker.app.controllers.pages;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.StudentTrackerApp;
import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.controllers.WindowController;
import io.github.unchangingconstant.studenttracker.app.controllers.components.EditableStudentTable;
import io.github.unchangingconstant.studenttracker.app.controllers.components.EditableVisitTable;
import io.github.unchangingconstant.studenttracker.app.controllers.components.SelectableStudentListView;
import io.github.unchangingconstant.studenttracker.app.controllers.components.StudentAdder;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import io.github.unchangingconstant.studenttracker.app.models.StudentTableModel;
import io.github.unchangingconstant.studenttracker.app.models.VisitTableModel;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService.IllegalDatabaseOperationException;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService.InvalidDatabaseEntryException;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/*
 * TODO Think about memory management. When this controller is garbage collected, do the listeners/bindings it adds 
 * to the Singleton StudentTableModel persist? 
 */
public class DatabaseManagerPageController implements Controller {
    
    @FXML
    private Label title;
    @FXML 
    private EditableStudentTable studentTable;
    @FXML
    private EditableVisitTable visitTable;
    @FXML
    private StudentAdder studentAdder;
    @FXML
    private HBox editorContainer;
    @FXML
    private SelectableStudentListView selectableStudentList;
    @FXML
    private Button exportButton;

    /*
     * MODELS
     */
    // State of the database 
    private StudentTableModel studentTableModel;
    private VisitTableModel visitTableModel;

    private AttendanceService attendanceService;

    @Inject
    public DatabaseManagerPageController(StudentTableModel studentTableModel, VisitTableModel visitTableModel, AttendanceService attendanceService)  {
        this.attendanceService = attendanceService;
        this.studentTableModel = studentTableModel;
        this.visitTableModel = visitTableModel;
    }

    @Override
    public void initialize() {
        studentTableModel.bindProperty(studentTable.itemsProperty());
        studentTable.onDeleteActionProperty().set(student -> onDeleteAction(student));
        studentTable.onSaveActionProperty().set(() -> onUpdateStudentAction());

        // Binds the visitTable to the visitTableModel
        visitTableModel.bindProperty(visitTable.itemsProperty());
        visitTable.currentStudentProperty().bind(visitTableModel.currentStudentProperty());

        // Binds selectableStudentList's currently selectedStudent to visitTableModel's currently selected student
        selectableStudentList.getFocusModel().focusedItemProperty().addListener((obs, oldVal, newVal) -> {
            visitTableModel.currentStudentProperty().set(newVal.getStudentId().get());
        });
        // Binds title to visitTableModel's currently selected student
        visitTableModel.currentStudentProperty().addListener((obs, oldVal, newVal) -> {
            title.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    String firstName = studentTableModel.getStudent(newVal.intValue()).getFullLegalName().get().split(" ")[0];
                    return "Viewing " + firstName + "'s attendance...";
                }, 
                studentTableModel.getStudent(newVal.intValue()).getFullLegalName()));
        });

        studentTableModel.bindProperty(selectableStudentList.itemsProperty());

        studentAdder.setOnSaveButtonAction(actionEvent -> onAddStudentAction());

        exportButton.setOnAction((actionEvent) -> {
            StudentTrackerApp.appContext.getInstance(WindowController.class).openExportDialog();
        });

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
        StudentModel update = studentTable.getEditedStudentModel();
        try {
            attendanceService.updateStudent(
            update.getStudentId().get(), 
            update.getFullLegalName().get(), 
            update.getPrefName().get(), 
            update.getSubjects().get());
        } catch (InvalidDatabaseEntryException e) {
            e.printStackTrace();
        }
        studentTable.editedRowIndexProperty().set(-1);
    }
}
