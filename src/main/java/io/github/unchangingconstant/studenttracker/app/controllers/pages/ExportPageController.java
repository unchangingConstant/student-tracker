package io.github.unchangingconstant.studenttracker.app.controllers.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.controllers.WindowController;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import io.github.unchangingconstant.studenttracker.app.models.StudentTableModel;
import io.github.unchangingconstant.studenttracker.app.services.ExportCSVService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.StringConverter;
import javafx.scene.control.SelectionMode;

public class ExportPageController implements Controller {

    @FXML
    private ListView<StudentModel> studentSelector;
    @FXML
    private Button exportButton;
    @FXML
    private Button cancelButton;

    // Represents students currently selected by selectionModel
    private final Map<StudentModel, BooleanProperty> selectionMap = new HashMap<>();

    private final StudentTableModel studentTableModel;
    private final ExportCSVService csvService;
    private final WindowController windowController;

    @Inject
    public ExportPageController(StudentTableModel studentTableModel, ExportCSVService csvService, WindowController windowController) {
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

    private void setupStudentSelector() {
        // Populates selection
        studentSelector.getItems().addAll(studentTableModel.getStudents());
        // Maps each student to a boolean property
        studentTableModel.getStudents().forEach(
            item -> selectionMap.put(item, new SimpleBooleanProperty(false)));

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
        // Handle CSV errors 
        csvService.exportStudentVisitsCSV(selectedStudentsIds);
    }

    private void onCancelButtonPress() {
        windowController.closeExportPage();
    }

}
