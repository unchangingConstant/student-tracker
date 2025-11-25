package io.github.unchangingconstant.studenttracker.app.controllers.custom;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import io.github.unchangingconstant.studenttracker.app.models.OngoingVisitModel;
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
 * 
 * Plan to implement time remaining:
 * 
 * Create a custom TableColumn that maintains a single timeline for all of its values.
 * 
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
    // Corresponds a list of students to the second their time remaining should be updated
    private Timeline timeline;

    public LiveAttendanceView() {
        super();
        CustomComponentUtils.hookIntoFXML(this, "/view/components/live_attendance_view.fxml");
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
        createTimeRemainingColumn();
        createActionsColumn();
    }

    private void createTimeRemainingColumn()    {
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimesRemaining()));
        
    }

    private void updateTimesRemaining() {
        Instant now = Instant.now();
        
    }

    private Long getTimeRemaining(OngoingVisitModel ongoingVisit) {
        return null;
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
