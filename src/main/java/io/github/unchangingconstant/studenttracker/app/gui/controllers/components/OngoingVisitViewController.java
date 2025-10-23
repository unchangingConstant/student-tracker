package io.github.unchangingconstant.studenttracker.app.gui.controllers.components;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Visit;
import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.viewmodels.SessionViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class OngoingVisitViewController implements Controller {

    @FXML
    private TableView<Visit> ongoingVisitView;

    private SessionViewModel viewModel;

    @Inject
    public OngoingVisitViewController(SessionViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize() {
        this.viewModel.bindToModelProperty(ongoingVisitView.itemsProperty());

        TableColumn<Visit, String> nameColumn = new TableColumn<>("Student Name");
        nameColumn.setCellValueFactory(visit -> {
            return new SimpleStringProperty("Some student");
        });
        TableColumn<Visit, String> timeRemainingColumn = new TableColumn<>("Time Remaining");
        nameColumn.setCellValueFactory(visit -> {
            return new SimpleStringProperty();
        });
        ongoingVisitView.getColumns().add(nameColumn);
        ongoingVisitView.getColumns().add(timeRemainingColumn);
    }
}
