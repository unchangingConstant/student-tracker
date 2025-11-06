package io.github.unchangingconstant.studenttracker.app.gui.components;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.github.unchangingconstant.studenttracker.app.backend.entities.OngoingVisit;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private final TableColumn<OngoingVisit, Number> actionsColumn = new TableColumn<>();

    // Upon action button press, passes the OngoingVisit's studentId to this consumer
    private Consumer<Integer> onButtonAction;
    // Corresponds studentId to ongoingVisit's time remaining
    private Map<Integer, SimpleLongProperty> timesRemaining;
    private Timeline timeline;

    public OngoingVisitView() {
        super();
        // Keeps an extra column at the end from rendering
        columnResizePolicyProperty().set(CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        timesRemaining = new HashMap<>();
        nameColumn.setText("Student Name");
        nameColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getStudentName());
        });
        startTimeColumn.setText("Start Time");
        startTimeColumn.setCellValueFactory(cellData -> {
            Instant startTime = cellData.getValue().getStartTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
            return new SimpleStringProperty(formatter.format(startTime));
        });
        createTimeRemainingColumn();
        createActionsColumn();

        List<TableColumn<OngoingVisit, ?>> columns = getColumns();
        columns.add(nameColumn);
        columns.add(timeRemainingColumn);
        columns.add(startTimeColumn);
        columns.add(actionsColumn);
    }

    public void setOnButtonAction(Consumer<Integer> newAction) {
        onButtonAction = newAction;
    }

    private void createTimeRemainingColumn()    {
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimesRemaining()));
        /**
         * Tried to fix the issue where the timeRemaining map doesn't update when the itemsProperty initially binds to the OngoingVisitTableModel. 
         * I guess invalidation listeners don't work here
         */
        InvalidationListener invalidationListener = (observable) -> System.out.println("Probably binding to the column");
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
        actionsColumn.setCellFactory(tableColumn -> {
            TableCell<OngoingVisit, Number> buttonCell = new TableCell<>();
            Button cellButton = new Button("End Visit");
            buttonCell.setGraphic(cellButton);
            cellButton.setOnAction(actionEvent ->  {
                if (onButtonAction != null) {
                    onButtonAction.accept(buttonCell.itemProperty().get().intValue()); // this code stinks
                }
            });
            return buttonCell;
        });
        actionsColumn.setCellValueFactory(cell ->   {
            return new SimpleIntegerProperty(cell.getValue().getStudentId());
        });
    }

}
