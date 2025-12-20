package io.github.unchangingconstant.studenttracker.app.controllers.pages;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.controllers.WindowController;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import io.github.unchangingconstant.studenttracker.app.models.StudentTableModel;
import io.github.unchangingconstant.studenttracker.app.services.ExportCSVService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    private StudentTableModel studentTableModel;
    private ExportCSVService csvService;
    private WindowController windowController;

    @Inject
    public ExportPageController(StudentTableModel studentTableModel, ExportCSVService csvService, WindowController windowController) {
        this.studentTableModel = studentTableModel;
        this.csvService = csvService;
        this.windowController = windowController;
    }

    @Override
    public void initialize() {
        studentTableModel.bindProperty(studentSelector.itemsProperty());

        studentSelector.setCellFactory(
            CheckBoxListCell.forListView(item -> new SimpleBooleanProperty(false),
            new StringConverter<StudentModel>() {
                @Override
                public String toString(StudentModel obj) {return obj.getFullLegalName().get();}
                // I'm not sure what leaving this null will do. I'm assuming this is for when the list
                // is edited (inline) and it needs to convert the other way. But since this list isn't being
                // edited, I'm going to assume this should be fine.
                @Override
                public StudentModel fromString(String string) {return null;}
            }));

        exportButton.setOnAction(actionEvent -> onExportButtonPress());
        cancelButton.setOnAction(actionEvent -> onCancelButtonPress());
    }

    private void onExportButtonPress() {
        System.out.println(studentSelector.getSelectionModel().getSelectedItems());

        //csvService.exportStudentVisitsCSV(selectedStudentsIds);
    }

    private void onCancelButtonPress() {
        windowController.closeExportPage();
    }

}
