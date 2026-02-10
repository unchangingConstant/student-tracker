package com.github.unchangingconstant.studenttracker.gui.pages;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.unchangingconstant.studenttracker.StudentTrackerApp;
import com.github.unchangingconstant.studenttracker.app.excelexport.ExcelExporter;
import com.github.unchangingconstant.studenttracker.gui.Controller;
import com.github.unchangingconstant.studenttracker.gui.WindowManager;
import com.github.unchangingconstant.studenttracker.gui.models.StudentModel;
import com.github.unchangingconstant.studenttracker.gui.models.StudentTableModel;
import com.github.unchangingconstant.studenttracker.gui.utils.ServiceTask;
import com.github.unchangingconstant.studenttracker.threads.ThreadManager;
import com.google.inject.Inject;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.StringConverter;

public class ExportPage implements Controller {

    @FXML
    private ListView<StudentModel> studentSelector;
    @FXML
    private Button exportButton;
    @FXML
    private Button cancelButton;
    @FXML
    private CheckBox selectAllCheckBox;

    // Represents students currently selected by selectionModel
    private final Map<StudentModel, BooleanProperty> selectionMap = new HashMap<>();

    private final StudentTableModel studentTableModel;
    private final ExcelExporter csvService;
    private final WindowManager windowController;

    @Inject
    public ExportPage(StudentTableModel studentTableModel, ExcelExporter csvService,
                      WindowManager windowController) {
        this.studentTableModel = studentTableModel;
        this.csvService = csvService;
        this.windowController = windowController;
    }

    @Override
    public void initialize() {
        setupStudentSelector();
        exportButton.setOnAction(actionEvent -> onExportButtonPress());
        cancelButton.setOnAction(actionEvent -> onCancelButtonPress());
    }

    // TODO ew, so large. Chop this up bruh
    private void setupStudentSelector() {
        // Makes list sorted alphabetically
        SortedList<StudentModel> studentsList = new SortedList<>(studentTableModel.unmodifiableStudentList(),
                new Comparator<StudentModel>() {
                    @Override
                    public int compare(StudentModel arg0, StudentModel arg1) {
                        return arg0.getFullName().get().compareTo(arg1.getFullName().get());
                    }
                });

        // Populates selection
        studentSelector.setItems(studentsList);

        // Maps each student to a boolean property
        studentTableModel.unmodifiableStudentList().forEach(
                item -> {
                    BooleanProperty boolProp = new SimpleBooleanProperty(false);
                    // If a single box is false, the selectAllCheckBox will also be false
                    boolProp.addListener((obs, oldVal, newVal) -> {
                        if (!newVal) {
                            selectAllCheckBox.setSelected(false);
                        }
                    });
                    selectionMap.put(item, boolProp);
                });

        // When selectAllCheckBox is clicked, all boxes in list must match its state
        selectAllCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            selectionMap.forEach((key, val) -> {
                val.set(newVal);
            });
        });

        // Sets up checkboxes
        studentSelector.setCellFactory(
            CheckBoxListCell.forListView(item -> selectionMap.get(item),
                new StringConverter<StudentModel>() {
                    @Override
                    public String toString(StudentModel obj) {
                        return obj.getFullName().get();
                    }

                    // I'm not sure what leaving this null will do. I'm assuming this is for when
                    // the list
                    // is edited (inline) and it needs to convert the other way. But since this list
                    // isn't being
                    // edited, I'm going to assume this should be fine.
                    @Override
                    public StudentModel fromString(String string) {
                        return null;
                    }
                }));
    }

    private void onExportButtonPress() {
        List<Integer> selectedStudentsIds = studentSelector.getItems().stream()
                .filter(student -> selectionMap.get(student).getValue())
                .map(student -> student.getStudentId().get())
                .toList();

        ServiceTask<Void> exportTask = new ServiceTask<Void>() {
            @Override
            protected Void call() throws Exception {
                csvService.exportStudentsVisitsToExcel(selectedStudentsIds);
                return null;
            }
        };

        // Creates dialog that displays upon task completion
        Alert exportDialog = new Alert(AlertType.INFORMATION);
        exportDialog.setTitle(StudentTrackerApp.TITLE);
        exportDialog.showingProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                windowController.closeExportPage();
            }
        });

        exportTask.stateProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.equals(Worker.State.SUCCEEDED)) {
                exportDialog.setHeaderText("Export successful!");
                exportDialog.showAndWait();
            } else if (newVal.equals(Worker.State.FAILED)) {
                exportDialog.setHeaderText("ERROR: Task failed");
                exportDialog.setAlertType(AlertType.ERROR);
                exportDialog.showAndWait();
            }
        });

        // TODO Handle exceptions
        ThreadManager.mainBackgroundExecutor().submit(exportTask);
    }

    private void onCancelButtonPress() {
        windowController.closeExportPage();
    }

}
