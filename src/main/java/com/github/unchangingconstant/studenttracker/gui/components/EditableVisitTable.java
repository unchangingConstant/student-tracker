package com.github.unchangingconstant.studenttracker.gui.components;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.github.unchangingconstant.studenttracker.gui.ComponentUtils;
import com.github.unchangingconstant.studenttracker.gui.Controller;
import com.github.unchangingconstant.studenttracker.gui.custom.EditableRowTable;
import com.github.unchangingconstant.studenttracker.gui.models.VisitModel;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

/**
 * I have a feeling this will need to be editable some day
 */
public class EditableVisitTable extends EditableRowTable<VisitModel, Integer> implements Controller {

    @FXML
    private TableColumn<VisitModel, String> startTimeColumn;
    @FXML
    private TableColumn<VisitModel, String> endTimeColumn;
    @FXML
    private TableColumn<VisitModel, Number> durationColumn;

    private final SimpleIntegerProperty currentStudent = new SimpleIntegerProperty(-1);
    public final SimpleIntegerProperty currentStudentProperty() {return currentStudent;}
    
    public EditableVisitTable() {
        super();
        ComponentUtils.hookIntoFXML(this, "/view/components/editable_visit_table.fxml");
    }

    public void initialize() {
        setupCellValueFactories();
        getColumns().remove(getControlColumn());
    }

    private void setupCellValueFactories() {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm").withZone(ZoneId.systemDefault());

        startTimeColumn.setCellValueFactory(cellData -> {
            ObjectProperty<Instant> startTime = cellData.getValue().getStartTime();
            StringProperty startTimeStr = new SimpleStringProperty(dateFormatter.format(startTime.get()));
            startTime.addListener((obs, oldVal, newVal) -> {
                startTimeStr.set(dateFormatter.format(newVal));
            });
            return startTimeStr;
        });

        endTimeColumn.setCellValueFactory(cellData -> {
            ObjectProperty<Instant> endTime = cellData.getValue().getEndTime();
            StringProperty endTimeStr = new SimpleStringProperty(dateFormatter.format(endTime.get()));
            endTime.addListener((obs, oldVal, newVal) -> {
                endTimeStr.set(dateFormatter.format(newVal));
            });
            return endTimeStr;
        });

        durationColumn.setCellValueFactory(cellData -> {
            SimpleLongProperty duration = new SimpleLongProperty(visitDuration(cellData.getValue()));
            cellData.getValue().getStartTime().addListener((obs, oldVal, newVal) -> duration.set(visitDuration(cellData.getValue())));
            cellData.getValue().getEndTime().addListener((obs, oldVal, newVal) -> duration.set(visitDuration(cellData.getValue())));
            return duration;
        });
    }

    private Long visitDuration(VisitModel visit) {
        return ChronoUnit.MINUTES.between(visit.getStartTime().get(), visit.getEndTime().get());
    }

    @Override
    protected Callback<CellDataFeatures<VisitModel, Integer>, ObservableValue<Integer>> getControlCellValueFactory() {
        return cellData -> cellData.getValue().getVisitId();
    }
    
}
