package com.github.unchangingconstant.studenttracker.gui.pages;

import java.time.Instant;
import java.util.Comparator;

import com.github.unchangingconstant.studenttracker.app.dbmanager.DatabaseManager;
import com.github.unchangingconstant.studenttracker.app.dbmanager.DatabaseManager.NoSuchEntityException;
import com.github.unchangingconstant.studenttracker.app.dbmanager.DatabaseManager.InvalidEntityException;
import com.github.unchangingconstant.studenttracker.app.entities.Student;
import com.github.unchangingconstant.studenttracker.gui.Controller;
import com.github.unchangingconstant.studenttracker.gui.WindowManager;
import com.github.unchangingconstant.studenttracker.gui.components.EditableStudentTable;
import com.github.unchangingconstant.studenttracker.gui.components.EditableVisitTable;
import com.github.unchangingconstant.studenttracker.gui.components.QRCodeTableView;
import com.github.unchangingconstant.studenttracker.gui.components.StudentAdder;
import com.github.unchangingconstant.studenttracker.gui.models.StudentModel;
import com.github.unchangingconstant.studenttracker.gui.models.StudentTableModel;
import com.github.unchangingconstant.studenttracker.gui.models.VisitModel;
import com.github.unchangingconstant.studenttracker.gui.models.VisitTableModel;
import com.github.unchangingconstant.studenttracker.gui.utils.ServiceTask;
import com.github.unchangingconstant.studenttracker.threads.ThreadManager;
import com.google.inject.Inject;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;

/*
 * TODO Think about memory management. If this controller is garbage collected, do the listeners/bindings it adds
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
    private final StudentTableModel studentTableModel;
    private final VisitTableModel visitTableModel;

    // Services / utils
    private final DatabaseManager recordManager;
    private final WindowManager windowManager;

    @Inject
    public DatabaseManagerPageController(
        StudentTableModel studentTableModel, 
        VisitTableModel visitTableModel, 
        DatabaseManager recordManager,
        WindowManager windowManager)  {
        this.recordManager = recordManager;
        this.studentTableModel = studentTableModel;
        this.visitTableModel = visitTableModel;
        this.windowManager = windowManager;
    }

    @Override
    public void initialize() {
        setupStudentManager();
        setupVisitView();
        setupQRCodeView();
        exportButton.setOnAction((actionEvent) -> {
            windowManager.openExportPage();
        });
    }

    public void onDeleteAction(Integer studentId) {
        ThreadManager.mainBackgroundExecutor().submit(new ServiceTask<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    recordManager.deleteStudent(studentId);
                } catch (NoSuchEntityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    public void onAddStudentAction()  {
        String fullName = studentAdder.fullNameProperty().get();
        String prefName = studentAdder.prefNameProperty().get();
        Integer visitTime = studentAdder.visitTimeProperty().get();

        ThreadManager.mainBackgroundExecutor().submit(new ServiceTask<Void>() {
            @Override
            protected Void call() throws Exception {
            try {
                recordManager.insertStudent(Student.builder()
                    .fullName(fullName)
                    .preferredName(prefName)
                    .visitTime(visitTime)
                    .dateAdded(Instant.now()).build());
            } catch (InvalidEntityException e) {
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
        String fullName = update.getFullName().get();
        String prefName = update.getPrefName().get();
        Integer subjects = update.getVisitTime().get();

        ThreadManager.mainBackgroundExecutor().submit(new ServiceTask<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    recordManager.updateStudent(Student.builder()
                        .studentId(studentId)
                        .fullName(fullName)
                        .preferredName(prefName)
                        .visitTime(subjects * 30).build());
                } catch (InvalidEntityException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> studentTable.editedRowIndexProperty().set(-1));
                return null;
            }
        });
    }

    private void setupStudentManager() {
        SortedList<StudentModel> sortedStudents = new SortedList<>(
            studentTableModel.unmodifiableStudentList(),
            new Comparator<StudentModel>() {
                @Override
                public int compare(StudentModel arg0, StudentModel arg1) {
                    return arg0.getFullName().get().compareTo(arg1.getFullName().get());
                }
            }
        );
        studentTable.setItems(sortedStudents);
        studentTable.onDeleteActionProperty().set(student -> onDeleteAction(student));
        studentTable.onSaveActionProperty().set(() -> onUpdateStudentAction());
        studentAdder.setOnSaveButtonAction(actionEvent -> onAddStudentAction());
    }

    private void setupVisitView() {
        // Creates list of visits bound to the visitTable model but sorted
        SortedList<VisitModel> sortedVisits = new SortedList<>(visitTableModel.unmodifiableVisitList(),
            new Comparator<VisitModel>() {
                @Override
                public int compare(VisitModel arg0, VisitModel arg1) {
                    return arg1.getStartTime().get().compareTo(arg0.getStartTime().get());
                }
            });
        // Visit table now based off sorted list
        visitTable.setItems(sortedVisits);
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
                    String firstName = studentTableModel.getStudent(newVal.intValue()).getFullName().get().split(" ")[0];
                    return "Viewing " + firstName + "'s attendance...";
                }, 
                studentTableModel.getStudent(newVal.intValue()).getFullName()));
            } else {
                title.setText("No student selected");
            }
        });

        SortedList<StudentModel> sortedStudents = new SortedList<>(
            studentTableModel.unmodifiableStudentList(),
            new Comparator<StudentModel>() {
                @Override
                public int compare(StudentModel arg0, StudentModel arg1) {
                    return arg0.getFullName().get().compareTo(arg1.getFullName().get());
                }
            }
        );
        selectableStudentList.setItems(sortedStudents);

        selectableStudentList.setCellFactory(listView -> {
            ListCell<StudentModel> cell = new ListCell<StudentModel>();
            Label cellContent = new Label();
            cell.itemProperty().addListener((obs, oldVal, newVal) -> {
                cellContent.textProperty().unbind();
                if (newVal != null) {
                    cellContent.textProperty().bind(cell.getItem().getFullName());
                }
            });
            cell.setGraphic(cellContent);
            return cell;
        });
    }

    private void setupQRCodeView() {
        SortedList<StudentModel> sortedStudents = new SortedList<>(
            studentTableModel.unmodifiableStudentList(),
            new Comparator<StudentModel>() {
                @Override
                public int compare(StudentModel arg0, StudentModel arg1) {
                    return arg0.getFullName().get().compareTo(arg1.getFullName().get());
                }
            }
        );
        qrCodeView.setItems(sortedStudents);

        qrCodeView.setOnCopyButtonAction(qrCode -> {
            ClipboardContent content = new ClipboardContent();
            content.put(DataFormat.PLAIN_TEXT, qrCode);
            Clipboard.getSystemClipboard().setContent(content);
        });
    }
}
