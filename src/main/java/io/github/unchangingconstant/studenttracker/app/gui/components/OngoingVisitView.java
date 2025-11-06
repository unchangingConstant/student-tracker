package io.github.unchangingconstant.studenttracker.app.gui.components;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.unchangingconstant.studenttracker.app.backend.entities.OngoingVisit;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import javafx.util.Duration;

/*
 * Used to view students current at the center
 * 
 * Plan to implement time remaining:
 * 
 * Create a custom TableColumn that maintains a single timeline for all of its values.
 * 
 */
public class OngoingVisitView extends TableView<OngoingVisit> {

    private final TableColumn<OngoingVisit, String> nameColumn = new TableColumn<>();
    private final TableColumn<OngoingVisit, Number> timeRemainingColumn = new TableColumn<>();
    private final TableColumn<OngoingVisit, String> startTimeColumn = new TableColumn<>();
    private final TableColumn<OngoingVisit, Void> actionsColumn = new TableColumn<>();

    // Corresponds studentId to ongoingVisit's time remaining
    private Map<Integer, SimpleLongProperty> timesRemaining;
    private Timeline timeline;

    public OngoingVisitView() {
        // Keeps an extra column at the end from rendering
        columnResizePolicyProperty().set(CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        timesRemaining = new HashMap<>();
        nameColumn.setText("Student Name");
        nameColumn.setCellValueFactory(visit -> {
            return new SimpleStringProperty(visit.getValue().getStudentName());
        });
        startTimeColumn.setText("Start Time");
        startTimeColumn.setCellValueFactory(visit -> {
            return new SimpleStringProperty(visit.getValue().getStartTime().toString());
        });
        createTimeRemainingColumn();
        createActionsColumn();

        List<TableColumn<OngoingVisit, ?>> columns = getColumns();
        columns.add(nameColumn);
        columns.add(timeRemainingColumn);
        columns.add(startTimeColumn);
        columns.add(actionsColumn);

    }

    private void createTimeRemainingColumn()    {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimesRemaining()));
        InvalidationListener invalidationListener = (observable) -> System.out.println("Probably binding to the column");
        /**
         * Tried to fix the issue where the timeRemaining map doesn't update when the itemsProperty binds to the OngoingVisitTableModel. 
         * I guess invalidation listeners don't work here
         */
        getItems().addListener(invalidationListener); 
        getItems().addListener(new ListChangeListener<OngoingVisit>()   {
            @Override
            public void onChanged(Change<? extends OngoingVisit> c) {
                if (c.wasAdded())  {
                    Instant now = Instant.now();
                    c.getAddedSubList().forEach(ongoingVisit -> {
                        timesRemaining.put(
                            ongoingVisit.getStudentId(), 
                            new SimpleLongProperty(ChronoUnit.MINUTES.between(now, ongoingVisit.getStartTime()))
                        );
                    });
                }
                else if (c.wasRemoved())    {
                    c.getRemoved().forEach(ongoingVisit ->  {
                        timesRemaining.remove(ongoingVisit.getStudentId());
                    });
                }
            }
        });
        timeRemainingColumn.setText("Time Remaining");
        timeRemainingColumn.setCellValueFactory(cell-> {
            return timesRemaining.get(cell.getValue().getStudentId());
        });
    }

    private void updateTimesRemaining() {
        Instant now = Instant.now();
        getItems().forEach(ongoingVisit ->  {
            timesRemaining.get(ongoingVisit.getStudentId()).set(ChronoUnit.MINUTES.between(now, ongoingVisit.getStartTime()));
        });
    }

    private void createActionsColumn()  {
        actionsColumn.setCellFactory(new Callback<TableColumn<OngoingVisit, Void>, TableCell<OngoingVisit, Void>>() {
            @Override
            public TableCell<OngoingVisit, Void> call(TableColumn<OngoingVisit, Void> col) {
                TableCell<OngoingVisit, Void> buttonCell = new TableCell<>();
                Button cellButton = new Button("Action");
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
