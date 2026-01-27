package com.github.unchangingconstant.studenttracker.gui.components;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.github.unchangingconstant.studenttracker.gui.ComponentUtils;
import com.github.unchangingconstant.studenttracker.gui.Controller;
import com.github.unchangingconstant.studenttracker.gui.custom.EditableRowTable;
import com.github.unchangingconstant.studenttracker.gui.custom.EditableRowTableCell;
import com.github.unchangingconstant.studenttracker.gui.models.StudentModel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class EditableStudentTable extends EditableRowTable<StudentModel, Integer> implements Controller {
    
    @FXML
    private TableColumn<StudentModel, Integer> studentIdColumn;
    @FXML
    private TableColumn<StudentModel, String> fullNameColumn;
    @FXML
    private TableColumn<StudentModel, String> prefNameColumn;
    @FXML
    private TableColumn<StudentModel, Number> visitTimeColumn;
    @FXML
    private TableColumn<StudentModel, String> dateAddedColumn;

    private final TextField fullLegalNameInput = new TextField();
    private final TextField prefNameInput = new TextField();
    private final ComboBox<Number> visitTimeInput = new ComboBox<>();

    private final StudentModel editedStudentModel = new StudentModel(-1,  "", "", null, 1);
    public final StudentModel getEditedStudentModel() {return editedStudentModel;}

    public EditableStudentTable() {
        super();
        ComponentUtils.hookIntoFXML(this, "/view/components/editable_student_table.fxml");
    }

    @Override
    public void initialize() {
        setupCellValueFactories();
        setupEditableCellFactories();
        // Puts the actions column at the end of the table
        getColumns().remove(getControlColumn());
        getColumns().add(getControlColumn());

        visitTimeInput.getItems().addAll(30, 60);

        editedRowIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() == -1) {
                return;
            }
            StudentModel student = getItems().get(newVal.intValue());
            editedStudentModel.getStudentId().set(student.getStudentId().get());
            editedStudentModel.getFullName().set(student.getFullName().get());
            editedStudentModel.getPrefName().set(student.getPrefName().get());
            editedStudentModel.getVisitTime().set(student.getVisitTime().get());
        });

        fullLegalNameInput.textProperty().bindBidirectional(editedStudentModel.getFullName());
        prefNameInput.textProperty().bindBidirectional(editedStudentModel.getPrefName());
        visitTimeInput.valueProperty().bindBidirectional(editedStudentModel.getVisitTime());
    }

    private void setupCellValueFactories() {
        studentIdColumn.setCellValueFactory(cellData -> cellData.getValue().getStudentId());
        fullNameColumn.setCellValueFactory(cellData -> cellData.getValue().getFullName());
        prefNameColumn.setCellValueFactory(cellData -> cellData.getValue().getPrefName());
        visitTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getVisitTime());

        dateAddedColumn.setCellValueFactory(cellData -> {
            Instant startTime = cellData.getValue().getDateAdded().get();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM dd yyyy").withZone(ZoneId.systemDefault());
            return new SimpleStringProperty(formatter.format(startTime));
        });
    }

    private void setupEditableCellFactories() {
        fullNameColumn.setCellFactory(tableColumn -> {
            EditableRowTableCell<StudentModel, String> cell = new EditableRowTableCell<>();
            cell.rowEditEnabledProperty().bind(editedRowIndexProperty().isEqualTo(cell.indexProperty()));
            cell.setEditEnabledGraphic(fullLegalNameInput);
            Label label = new Label();
            label.textProperty().bind(cell.itemProperty());
            cell.setEditDisabledGraphic(label);
            return cell;
        });
        prefNameColumn.setCellFactory(tableColumn -> {
            EditableRowTableCell<StudentModel, String> cell = new EditableRowTableCell<>();
            cell.rowEditEnabledProperty().bind(editedRowIndexProperty().isEqualTo(cell.indexProperty()));
            cell.setEditEnabledGraphic(prefNameInput);
            Label label = new Label();
            label.textProperty().bind(cell.itemProperty());
            cell.setEditDisabledGraphic(label);
            return cell;
        });
        visitTimeColumn.setCellFactory(tableColumn -> {
            EditableRowTableCell<StudentModel, Number> cell = new EditableRowTableCell<>();
            cell.rowEditEnabledProperty().bind(editedRowIndexProperty().isEqualTo(cell.indexProperty()));
            cell.setEditEnabledGraphic(visitTimeInput);
            Label label = new Label();
            label.textProperty().bind(cell.itemProperty().asString());
            cell.setEditDisabledGraphic(label);
            return cell;
        });
    }

    @Override
    protected Callback<CellDataFeatures<StudentModel, Integer>, ObservableValue<Integer>> getControlCellValueFactory() {
        return cellData -> cellData.getValue().getStudentId();
    }

}
