package io.github.unchangingconstant.studenttracker.app.controllers;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.entities.Visit;
import io.github.unchangingconstant.studenttracker.app.viewmodels.OngoingVisitsViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class OngoingVisitTableController implements Controller {

    @FXML
    private TableView<Visit> ongoingVisitsView;

    private OngoingVisitsViewModel viewModel;

    @Inject
    public OngoingVisitTableController(OngoingVisitsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize() {
        TableColumn<Visit, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory((visit) -> {
            return new SimpleStringProperty(visit.getValue().getStudentName());
        });
        TableColumn<Visit, Number> timeCol = new TableColumn<>("Time Remaining");
        timeCol.setCellValueFactory((visit) -> {
            return viewModel.getTimeRemaining().get(visit.getValue().getVisitId());
        });
    }

}
