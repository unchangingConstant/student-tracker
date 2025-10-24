package io.github.unchangingconstant.studenttracker.app.gui.controllers.components;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.OngoingVisit;
import io.github.unchangingconstant.studenttracker.app.backend.entities.Visit;
import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.viewmodels.SessionViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class OngoingVisitViewController implements Controller {

    @FXML
    private TableView<OngoingVisit> ongoingVisitView;
    @FXML
    private TableColumn<OngoingVisit, String> nameColumn;
    @FXML
    private TableColumn<OngoingVisit, Number> timeRemainingColumn;
    @FXML
    private TableColumn<OngoingVisit, String> startTimeColumn;
    @FXML
    private TableColumn<OngoingVisit, Void> actionsColumn;

    private SessionViewModel viewModel;

    @Inject
    public OngoingVisitViewController(SessionViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize() {
        this.viewModel.bindToModelProperty(ongoingVisitView.itemsProperty());
        nameColumn.setCellValueFactory(visit -> {
            return new SimpleStringProperty(visit.getValue().getStudentName());
        });
        timeRemainingColumn.setCellValueFactory(visit -> {
            return viewModel.getTimeRemainingRef().get(visit.getValue().getStudentId());
        });
        startTimeColumn.setCellValueFactory(visit -> {
            return new SimpleStringProperty(visit.getValue().getStartTime().toString());
        });
        actionsColumn.setCellFactory(new Callback<TableColumn<OngoingVisit, Void>, TableCell<OngoingVisit, Void>>() {
            @Override
            public TableCell<OngoingVisit, Void> call(TableColumn<OngoingVisit, Void> col) {
                TableCell<OngoingVisit, Void> buttonCell = new TableCell<>();
                Button cellButton = new Button("Poop");
                buttonCell.setGraphic(cellButton);
                cellButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println(event.getEventType());
                    }
                });
                return buttonCell;
            }
        });
    }
}
