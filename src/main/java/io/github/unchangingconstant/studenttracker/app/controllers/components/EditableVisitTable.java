package io.github.unchangingconstant.studenttracker.app.controllers.components;

import java.time.Instant;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import io.github.unchangingconstant.studenttracker.app.controllers.custom.EditableRowTable;
import io.github.unchangingconstant.studenttracker.app.models.VisitModel;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class EditableVisitTable extends EditableRowTable<VisitModel, Integer> implements Controller {

    @FXML
    private TableColumn<VisitModel, Integer> visitIdColumn;
    @FXML
    private TableColumn<VisitModel, String> studentColumn;
    @FXML
    private TableColumn<VisitModel, String> startTimeColumn;
    @FXML
    private TableColumn<VisitModel, String> endTimeColumn;

    public EditableVisitTable() {
        super();
        CustomComponentUtils.hookIntoFXML(this, "/view/components/editable_visit_table.fxml");
    }

    public void initialize() {
        setupCellValueFactories();
        setupEditableCellFactories();
        // Puts the actions column at the end of the table
        getColumns().remove(getControlColumn());
        getControlColumn().getColumns().add(getControlColumn());
    }

    private void setupCellValueFactories() {
        visitIdColumn.setCellValueFactory(cellData -> cellData.getValue().getVisitId());
        startTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getEndTime().asString());
        endTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getStartTime().asString());
    }

    private void setupEditableCellFactories() {
    }

    @Override
    protected Callback<CellDataFeatures<VisitModel, Integer>, ObservableValue<Integer>> getControlCellValueFactory() {
        return cellData -> cellData.getValue().getVisitId();
    }
    
}
