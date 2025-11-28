package io.github.unchangingconstant.studenttracker.app.controllers.components;

import java.time.temporal.ChronoUnit;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import io.github.unchangingconstant.studenttracker.app.controllers.custom.EditableRowTable;
import io.github.unchangingconstant.studenttracker.app.models.VisitModel;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class EditableVisitTable extends EditableRowTable<VisitModel, Integer> implements Controller {

    @FXML
    private TableColumn<VisitModel, String> titleColumn;
    @FXML
    private TableColumn<VisitModel, String> startTimeColumn;
    @FXML
    private TableColumn<VisitModel, String> endTimeColumn;
    @FXML
    private TableColumn<VisitModel, Number> durationColumn;

    private final SimpleIntegerProperty currentStudent = new SimpleIntegerProperty(-1);
    public final SimpleIntegerProperty currentStudentProperty() {return currentStudent;}

    public final StringProperty titleProperty() {return titleColumn.textProperty();}

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
        startTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getEndTime().asString());
        endTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getStartTime().asString());
        durationColumn.setCellValueFactory(cellData -> {
            VisitModel visit = cellData.getValue();
            return new SimpleLongProperty(ChronoUnit.MINUTES.between(visit.getStartTime().get(), visit.getEndTime().get()));
        });
    }

    private void setupEditableCellFactories() {
    }

    @Override
    protected Callback<CellDataFeatures<VisitModel, Integer>, ObservableValue<Integer>> getControlCellValueFactory() {
        return cellData -> cellData.getValue().getVisitId();
    }
    
}
