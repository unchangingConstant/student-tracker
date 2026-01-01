package com.github.unchangingconstant.studenttracker.gui.pages;

import java.util.List;

import com.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import com.github.unchangingconstant.studenttracker.app.services.ExportExcelService;
import com.github.unchangingconstant.studenttracker.app.services.AttendanceService.IllegalDatabaseOperationException;
import com.github.unchangingconstant.studenttracker.app.services.AttendanceService.InvalidDatabaseEntryException;
import com.github.unchangingconstant.studenttracker.gui.Controller;
import com.github.unchangingconstant.studenttracker.gui.WindowManager;
import com.github.unchangingconstant.studenttracker.gui.components.EditableStudentTable;
import com.github.unchangingconstant.studenttracker.gui.components.EditableVisitTable;
import com.github.unchangingconstant.studenttracker.gui.components.QRCodeTableView;
import com.github.unchangingconstant.studenttracker.gui.components.StudentAdder;
import com.github.unchangingconstant.studenttracker.gui.models.StudentModel;
import com.github.unchangingconstant.studenttracker.gui.models.StudentTableModel;
import com.github.unchangingconstant.studenttracker.gui.models.VisitTableModel;
import com.github.unchangingconstant.studenttracker.gui.taskutils.ServiceTask;
import com.github.unchangingconstant.studenttracker.threads.ThreadManager;
import com.google.inject.Inject;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
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
    private ListView<StudentModel> selectableStudentList;
    @FXML
    private Button exportButton;
    @FXML
    private QRCodeTableView qrCodeView;

    /*
     * MODELS
     */
    // State of the database 
    private StudentTableModel studentTableModel;
    private VisitTableModel visitTableModel;

    // Services / utils
    private AttendanceService attendanceService;
    private WindowManager windowController;

    @Inject
    public DatabaseManagerPageController(
        StudentTableModel studentTableModel, 
        VisitTableModel visitTableModel, 
        AttendanceService attendanceService, 
        WindowManager windowController,
        ExportExcelService csvService)  {
        this.attendanceService = attendanceService;
        this.studentTableModel = studentTableModel;
        this.visitTableModel = visitTableModel;
        this.windowController = windowController;
    }

    @Override
    public void initialize() {
        studentTableModel.bindProperty(studentTable.itemsProperty());
        studentTable.onDeleteActionProperty().set(student -> onDeleteAction(student));
        studentTable.onSaveActionProperty().set(() -> onUpdateStudentAction());

        // Binds the visitTable to the visitTableModel
        visitTableModel.bindProperty(visitTable.itemsProperty());
        visitTable.currentStudentProperty().bind(visitTableModel.currentStudentProperty()); // this stinks

        // Binds selectableStudentList's currently selectedStudent to visitTableModel's currently selected student
        selectableStudentList.getFocusModel().focusedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                visitTableModel.currentStudentProperty().set(newVal.getStudentId().get());
            } else {
                visitTableModel.currentStudentProperty().set(-1);
            }
        });
        // Binds title to visitTableModel's currently selected student
        visitTableModel.currentStudentProperty().addListener((obs, oldVal, newVal) -> {
            title.textProperty().unbind();
            if (!newVal.equals(-1)) {
                title.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    String firstName = studentTableModel.getStudent(newVal.intValue()).getFullLegalName().get().split(" ")[0];
                    return "Viewing " + firstName + "'s attendance...";
                }, 
                studentTableModel.getStudent(newVal.intValue()).getFullLegalName()));
            } else {
                title.setText("No student selected");
            }
        });

        studentTableModel.bindProperty(selectableStudentList.itemsProperty());

        studentAdder.setOnSaveButtonAction(actionEvent -> onAddStudentAction());

        exportButton.setOnAction((actionEvent) -> {
            windowController.openExportPage();
        });

        selectableStudentList.setCellFactory(listView -> {
            ListCell<StudentModel> cell = new ListCell<StudentModel>();
            Label cellContent = new Label();
            cell.itemProperty().addListener((obs, oldVal, newVal) -> {
                cellContent.textProperty().unbind();
                if (newVal != null) {
                    cellContent.textProperty().bind(cell.getItem().getFullLegalName());
                }
            });
            cell.setGraphic(cellContent);
            return cell;
        });

        studentTableModel.bindProperty(qrCodeView.itemsProperty());

        qrCodeView.setOnCopyButtonAction(qrCode -> {
            ClipboardContent content = new ClipboardContent();
            content.put(DataFormat.PLAIN_TEXT, qrCode);
            Clipboard.getSystemClipboard().setContent(content);
        });

        
    }

    public void onDeleteAction(Integer studentId) {
        ThreadManager.mainBackgroundExecutor().submit(new ServiceTask<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    attendanceService.deleteStudent(studentId);
                } catch (IllegalDatabaseOperationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    public void onAddStudentAction()  {
        String fullName = studentAdder.fullLegalNameProperty().get();
        String prefName = studentAdder.prefNameProperty().get();
        Integer subjects = studentAdder.subjectsProperty().get();

        ThreadManager.mainBackgroundExecutor().submit(new ServiceTask<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    attendanceService.insertStudent(fullName, prefName, subjects);
                } catch (InvalidDatabaseEntryException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Platform.runLater(() -> studentAdder.addingEnabledProperty().set(false));
                return null;
            }
        });
    }

    public void onUpdateStudentAction() {
        StudentModel update = studentTable.getEditedStudentModel();
        Integer studentId = update.getStudentId().get();
        String fullName = update.getFullLegalName().get();
        String prefName = update.getPrefName().get();
        Integer subjects = update.getSubjects().get();

        ThreadManager.mainBackgroundExecutor().submit(new ServiceTask<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    attendanceService.updateStudent(studentId, fullName, prefName, subjects);
                } catch (InvalidDatabaseEntryException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> studentTable.editedRowIndexProperty().set(-1));
                return null;
            }
        });
    }
}
