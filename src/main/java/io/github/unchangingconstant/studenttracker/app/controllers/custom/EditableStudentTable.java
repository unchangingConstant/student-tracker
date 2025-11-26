package io.github.unchangingconstant.studenttracker.app.controllers.custom;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import io.github.unchangingconstant.studenttracker.app.controllers.components.EditableRowTable;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class EditableStudentTable extends EditableRowTable<StudentModel, Integer> implements Controller {
    
    @FXML
    private TableColumn<StudentModel, Integer> studentIdColumn;
    @FXML
    private TableColumn<StudentModel, String> fullLegalNameColumn;
    @FXML
    private TableColumn<StudentModel, String> prefNameColumn;
    @FXML
    private TableColumn<StudentModel, Integer> subjectsColumn;
    @FXML
    private TableColumn<StudentModel, String> dateAddedColumn;

    public EditableStudentTable() {
        super();
        CustomComponentUtils.hookIntoFXML(this, "/view/components/editable_student_table.fxml");
    }

    @Override
    public void initialize() {

        fullLegalNameColumn.setCellValueFactory(cellData -> {
            return cellData.getValue().getFullLegalName();
        });

        // Puts the actions column at the end of the table
        getColumns().remove(getActionsColumn());
        getColumns().add(getActionsColumn());
    }

    @Override
    protected Callback<CellDataFeatures<StudentModel, Integer>, ObservableValue<Integer>> getControlCellValueFactory() {
        return cellData -> cellData.getValue().getStudentId();
    }

}
