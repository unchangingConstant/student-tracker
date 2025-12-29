package com.github.unchangingconstant.studenttracker.gui.components;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

import com.github.unchangingconstant.studenttracker.gui.ComponentUtils;
import com.github.unchangingconstant.studenttracker.gui.Controller;
import com.github.unchangingconstant.studenttracker.gui.models.OngoingVisitModel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Duration;

/*
 * Used to view students current at the center
 */
public class LiveAttendanceView extends TableView<OngoingVisitModel> implements Controller {

    @FXML
    private TableColumn<OngoingVisitModel, String> nameColumn;
    @FXML
    private TableColumn<OngoingVisitModel, Number> timeRemainingColumn;
    @FXML
    private TableColumn<OngoingVisitModel, String> startTimeColumn;
    @FXML
    private TableColumn<OngoingVisitModel, Number> actionsColumn;

    // Upon action button press, passes the OngoingVisit's studentId to this consumer
    private Consumer<Integer> onButtonAction;
    public void setOnButtonAction(Consumer<Integer> newAction) {onButtonAction = newAction;}
    private final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimesRemaining()));


    public LiveAttendanceView() {
        super();
        ComponentUtils.hookIntoFXML(this, "/view/components/live_attendance_view.fxml");
    }

    @Override
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getStudentName().get());
        });
        startTimeColumn.setCellValueFactory(cellData -> {
            Instant startTime = cellData.getValue().getStartTime().get();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
            return new SimpleStringProperty(formatter.format(startTime));
        });
        timeRemainingColumn.setCellValueFactory(cellData -> {
            return cellData.getValue().getTimeRemaining();
        });
        createActionsColumn();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    private void updateTimesRemaining() {
        Instant now = Instant.now();
        for (OngoingVisitModel visit: getItems()) {
            visit.getTimeRemaining().set((visit.getSubjects().get() * 30) - ChronoUnit.MINUTES.between(visit.getStartTime().get(), now));
        }
    }

    private void createActionsColumn()  {
        actionsColumn.setCellFactory(tableColumn -> {
            TableCell<OngoingVisitModel, Number> buttonCell = new TableCell<>();
            Button cellButton = new Button("End Visit");
            buttonCell.setGraphic(cellButton);
            cellButton.setOnAction(actionEvent ->  {
                if (onButtonAction != null) {
                    onButtonAction.accept(buttonCell.itemProperty().get().intValue());
                }
            });
            // Button is only visible when the row has an item
            cellButton.visibleProperty().bind(buttonCell.itemProperty().isNotNull());
            return buttonCell;
        });
        actionsColumn.setCellValueFactory(cell ->   {
            return new SimpleIntegerProperty(cell.getValue().getStudentId().get());
        });
    }
    
    // Makes this component un-focusable
    @Override
    public void requestFocus()  {}

}
