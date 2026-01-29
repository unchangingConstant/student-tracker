package com.github.unchangingconstant.studenttracker.gui.components;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.github.unchangingconstant.studenttracker.gui.ComponentUtils;
import com.github.unchangingconstant.studenttracker.gui.Controller;
import com.github.unchangingconstant.studenttracker.gui.custom.EditableRowTable;
import com.github.unchangingconstant.studenttracker.gui.models.VisitModel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

/**
 * I have a feeling this will need to be editable some day
 */
public class EditableVisitTable extends EditableRowTable<VisitModel, Number> implements Controller {

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
            ObjectProperty<Instant> startTimeProp = cellData.getValue().getStartTime();
            return Bindings.createStringBinding(
                () -> {
                    return dateFormatter.format(startTimeProp.get());
                }, startTimeProp
            );
        });

        endTimeColumn.setCellValueFactory(cellData -> {
            VisitModel visitModel = cellData.getValue();
            ObjectProperty<Instant> startTimeProp = visitModel.getStartTime();
            IntegerProperty durationProp = visitModel.getDuration();
            return Bindings.createStringBinding(
                () -> {
                    Instant endTime = startTimeProp.get().plus(Duration.ofMinutes(durationProp.get()));
                    return dateFormatter.format(endTime);
                },  startTimeProp, durationProp
            );
        });

        durationColumn.setCellValueFactory(cellData -> cellData.getValue().getDuration());
    }

    @Override
    protected Callback<CellDataFeatures<VisitModel, Number>, ObservableValue<Number>> getControlCellValueFactory() {
        return cellData -> cellData.getValue().getVisitId();
    }
    
}
