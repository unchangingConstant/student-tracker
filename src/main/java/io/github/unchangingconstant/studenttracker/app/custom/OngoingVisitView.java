package io.github.unchangingconstant.studenttracker.app.custom;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import io.github.unchangingconstant.studenttracker.app.domain.OngoingVisitDomain;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
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
public class OngoingVisitView extends TableView<OngoingVisitDomain> implements Controller {

    @FXML
    private TableColumn<OngoingVisitDomain, String> nameColumn;
    @FXML
    private TableColumn<OngoingVisitDomain, Number> timeRemainingColumn;
    @FXML
    private TableColumn<OngoingVisitDomain, String> startTimeColumn;
    @FXML
    private TableColumn<OngoingVisitDomain, Number> actionsColumn;

    // Upon action button press, passes the OngoingVisit's studentId to this consumer
    private Consumer<Integer> onButtonAction;
    public void setOnButtonAction(Consumer<Integer> newAction) {onButtonAction = newAction;}

    // Corresponds studentId to ongoingVisit's time remaining
    private Map<Integer, SimpleLongProperty> timesRemaining;
    private Timeline timeline;

    public OngoingVisitView() {
        super();
        timesRemaining = new HashMap<>();
        CustomComponentUtils.hookIntoFXML(this, "/view/components/ongoing_visit_view.fxml");
    }

    @Override
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getStudentName());
        });
        startTimeColumn.setCellValueFactory(cellData -> {
            Instant startTime = cellData.getValue().getStartTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
            return new SimpleStringProperty(formatter.format(startTime));
        });
        createTimeRemainingColumn();
        createActionsColumn();
    }

    private void createTimeRemainingColumn()    {
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimesRemaining()));
        /**
         * Tried to fix the issue where the timeRemaining map doesn't update when the itemsProperty initially binds to the OngoingVisitTableModel. 
         * I guess invalidation listeners don't work here
         */
        InvalidationListener invalidationListener = (observable) -> System.out.println("Probably binding to the column");
        getItems().addListener(invalidationListener); 
        getItems().addListener(new ListChangeListener<OngoingVisitDomain>()   {
            @Override
            public void onChanged(Change<? extends OngoingVisitDomain> c) {
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
            TableCell<OngoingVisitDomain, Number> buttonCell = new TableCell<>();
            Button cellButton = new Button("End Visit");
            buttonCell.setGraphic(cellButton);
            cellButton.setOnAction(actionEvent ->  {
                if (onButtonAction != null) {
                    onButtonAction.accept(buttonCell.itemProperty().get().intValue()); // this code stinks
                }
            });
            // Button is only visible when the row has an item
            cellButton.visibleProperty().bind(buttonCell.itemProperty().isNotNull());
            return buttonCell;
        });
        actionsColumn.setCellValueFactory(cell ->   {
            return new SimpleIntegerProperty(cell.getValue().getStudentId());
        });
    }
    
    // Makes this component un-focusable
    @Override
    public void requestFocus()  {}

}
