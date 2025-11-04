package io.github.unchangingconstant.studenttracker.app.gui.components;

import io.github.unchangingconstant.studenttracker.app.backend.entities.OngoingVisit;
import io.github.unchangingconstant.studenttracker.app.gui.viewmodels.SessionViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/*
 * Used to view students current at the center
 */
public class OngoingVisitView extends TableView<OngoingVisit> {


    private final TableColumn<OngoingVisit, String> nameColumn = new TableColumn<>();
    private final TableColumn<OngoingVisit, Number> timeRemainingColumn = new TableColumn<>();
    private final TableColumn<OngoingVisit, String> startTimeColumn = new TableColumn<>();
    private final TableColumn<OngoingVisit, Void> actionsColumn = new TableColumn<>();

    public OngoingVisitView() {
        nameColumn.setCellValueFactory(visit -> {
            return new SimpleStringProperty(visit.getValue().getStudentName());
        });
        // Handle this logic at the component level blud, long time coming
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
