package io.github.unchangingconstant.studenttracker.gui.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.services.ExportExcelService;
import io.github.unchangingconstant.studenttracker.gui.Controller;
import io.github.unchangingconstant.studenttracker.gui.WindowController;
import io.github.unchangingconstant.studenttracker.gui.models.StudentModel;
import io.github.unchangingconstant.studenttracker.gui.models.StudentTableModel;
import io.github.unchangingconstant.studenttracker.gui.taskutils.ServiceTask;
import io.github.unchangingconstant.studenttracker.threads.ThreadManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.StringConverter;

public class ExportPageController implements Controller {

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
    private final ExportExcelService csvService;
    private final WindowController windowController;

    @Inject
    public ExportPageController(StudentTableModel studentTableModel, ExportExcelService csvService, WindowController windowController) {
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
        // Populates selection
        studentSelector.getItems().addAll(studentTableModel.getStudents());

        // Maps each student to a boolean property
        studentTableModel.getStudents().forEach(
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
            public String toString(StudentModel obj) {return obj.getFullLegalName().get();}
            // I'm not sure what leaving this null will do. I'm assuming this is for when the list
            // is edited (inline) and it needs to convert the other way. But since this list isn't being
            // edited, I'm going to assume this should be fine.
            @Override
            public StudentModel fromString(String string) {return null;}
        }));
    }

    private void onExportButtonPress() {
        List<Integer> selectedStudentsIds = studentSelector.getItems().stream()
            .filter(student -> selectionMap.get(student).getValue())
            .map(student -> student.getStudentId().get())
            .toList();
        // TODO Handle exceptions
        ThreadManager.mainBackgroundExecutor().submit(new ServiceTask<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    csvService.exportStudentsVisitsToExcel(selectedStudentsIds);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    private void onCancelButtonPress() {
        windowController.closeExportPage();
    }

}
