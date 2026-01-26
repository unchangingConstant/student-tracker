package com.github.unchangingconstant.studenttracker.gui.components;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

import com.github.unchangingconstant.studenttracker.gui.ComponentUtils;
import com.github.unchangingconstant.studenttracker.gui.Controller;
import com.github.unchangingconstant.studenttracker.gui.models.LiveVisitModel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Duration;

/*
 * Used to view students current at the center
 */
public class LiveAttendanceView extends TableView<LiveVisitModel> implements Controller {

    @FXML
    private TableColumn<LiveVisitModel, String> nameColumn;
    @FXML
    private TableColumn<LiveVisitModel, Number> timeRemainingColumn;
    @FXML
    private TableColumn<LiveVisitModel, String> startTimeColumn;
    @FXML
    private TableColumn<LiveVisitModel, Number> actionsColumn;

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
            return cellData.getValue().getStudentName();
        });
        startTimeColumn.setCellValueFactory(cellData -> {
            Property<Instant> startTimeProp = cellData.getValue().getStartTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
            return Bindings.createStringBinding(
                () -> {
                    return formatter.format(startTimeProp.getValue());
                }, startTimeProp
            );
        });
        timeRemainingColumn.setCellValueFactory(cellData -> {
            return cellData.getValue().getTimeRemaining();
        });
        createActionsColumn();
        setRowFactory(tableView -> {
            return new LiveAttendanceViewTableRow();
        });
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    private void updateTimesRemaining() {
        Instant now = Instant.now();
        for (LiveVisitModel visit: getItems()) {
            visit.getTimeRemaining().set((visit.getVisitTime().get()) - ChronoUnit.MINUTES.between(visit.getStartTime().get(), now));
        }
    }

    private void createActionsColumn()  {
        actionsColumn.setCellFactory(tableColumn -> {
            TableCell<LiveVisitModel, Number> buttonCell = new TableCell<>();
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

    /**
     * Adds pseudo class to the cells in the time remaining column so that they may be
     * styled with CSS (See ISSUE#28)
     */
    static class LiveAttendanceViewTableRow extends TableRow<LiveVisitModel> {

        enum VisitCompletionStatus {DEFAULT, ALMOST_DONE, OVER}

        // No VISITING pseudoclass. It will be the default state
        private static final PseudoClass ALMOST_DONE = PseudoClass.getPseudoClass("almostDone");
        private static final PseudoClass OVER = PseudoClass.getPseudoClass("over");

        /**
         * This property reflects this row's pseudoclass state.
         * Upon invalidation, the pseudoClassStates are changed
         */
        private final ObjectProperty<VisitCompletionStatus> completionStatus = 
            new SimpleObjectProperty<>(this, "completionStatus", VisitCompletionStatus.DEFAULT) {
                @Override
                protected void invalidated() {
                    super.invalidated();
                    pseudoClassStateChanged(ALMOST_DONE, completionStatus.get() == VisitCompletionStatus.ALMOST_DONE);
                    pseudoClassStateChanged(OVER, completionStatus.get() == VisitCompletionStatus.OVER);
                }
            };

        public LiveAttendanceViewTableRow() {
            super();
            /**
             * Binds completionStatus to the subjects and time remaining.
             * When the student has one sixth of their time remaining, the completion status is ALMOST_DONE
             * When the student is one sixth of their visit time over, the completion status is OVER
             * Otherwise, it's just visiting.
             */
            itemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null) {
                    completionStatus.unbind();
                    completionStatus.set(VisitCompletionStatus.DEFAULT);
                } else {
                    IntegerProperty visitTime = itemProperty().get().getVisitTime();
                    LongProperty timeRemaining = itemProperty().get().getTimeRemaining();
                    completionStatus.bind(Bindings.createObjectBinding(
                        () -> {
                            // Null checks
                            if (visitTime == null || timeRemaining == null) {
                                return VisitCompletionStatus.DEFAULT;
                            }
                            // now the real stuff
                            int oneSixthVisitTime = (visitTime.get()) / 6;
                            if (timeRemaining.get() <= (-1) * oneSixthVisitTime) {
                                return VisitCompletionStatus.OVER;
                            } else if (timeRemaining.get() <= oneSixthVisitTime) {
                                return VisitCompletionStatus.ALMOST_DONE;
                            } else {
                                return VisitCompletionStatus.DEFAULT;
                            }
                        },
                        visitTime, timeRemaining)
                    );
                }
            });
        }

    }
    
    // Makes this component un-focusable
    @Override
    public void requestFocus()  {}

}
