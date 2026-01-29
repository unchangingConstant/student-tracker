package com.github.unchangingconstant.studenttracker.gui.pages;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

import com.github.unchangingconstant.studenttracker.app.dbmanager.DatabaseManager;
import com.github.unchangingconstant.studenttracker.app.entities.OngoingVisit;
import com.github.unchangingconstant.studenttracker.gui.Controller;
import com.github.unchangingconstant.studenttracker.gui.WindowManager;
import com.github.unchangingconstant.studenttracker.gui.components.LiveAttendanceView;
import com.github.unchangingconstant.studenttracker.gui.components.StudentSelector;
import com.github.unchangingconstant.studenttracker.gui.models.LiveVisitModel;
import com.github.unchangingconstant.studenttracker.gui.models.LiveAttendanceDashboardModel;
import com.github.unchangingconstant.studenttracker.gui.models.StudentModel;
import com.github.unchangingconstant.studenttracker.gui.models.StudentTableModel;
import com.github.unchangingconstant.studenttracker.gui.utils.ServiceTask;
import com.github.unchangingconstant.studenttracker.threads.ThreadManager;
import com.google.inject.Inject;

import javafx.application.Platform;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class AttendanceDashboardPageController implements Controller {

    @FXML
    private LiveAttendanceView liveAttendanceView;
    @FXML
    private StudentSelector studentSelector; 
    @FXML
    private Button startVisitButton;
    @FXML
    private Menu menuButton;
    @FXML
    private MenuItem recordManagerMenuItem;

    private final LiveAttendanceDashboardModel ongoingVisitsModel;
    private final StudentTableModel studentTableModel;
    private final DatabaseManager recordManager;
    private final WindowManager windowController;

    @Inject
    public AttendanceDashboardPageController(
        LiveAttendanceDashboardModel ongoingVisitsModel,
        StudentTableModel studentTableModel, 
        DatabaseManager recordManager,
        WindowManager windowController)  {
        this.ongoingVisitsModel = ongoingVisitsModel;
        this.studentTableModel = studentTableModel;
        this.recordManager = recordManager;
        this.windowController = windowController;
    }

    @Override
    public void initialize() {
        studentTableModel.bindList(studentSelector.optionsProperty());
        startVisitButton.setOnAction(actionEvent -> {
            StudentModel selected = studentSelector.selectedProperty().getValue();
            if (selected != null)  {
                onStartVisitAction(selected);
            }
        });
        // Makes table sorted by times remaining
        SortedList<LiveVisitModel> liveAttendanceList =
            new SortedList<>(ongoingVisitsModel.unmodifiableOngoingVisitList(),
            new Comparator<LiveVisitModel>() {
                @Override
                public int compare(LiveVisitModel arg0, LiveVisitModel arg1) {
                    return arg1.getTimeRemaining().get() < arg0.getTimeRemaining().get() ? 1 : -1;
                }
            }
        );
        liveAttendanceView.setItems(liveAttendanceList);

        liveAttendanceView.setOnButtonAction(this::onEndOngoingVisit);
        recordManagerMenuItem.setOnAction(actionEvent -> windowController.openRecordManager());
    }

    public void onStartVisitAction(StudentModel selectedStudent) {
        Integer selectedStudentId = selectedStudent.getStudentId().get();
        ThreadManager.mainBackgroundExecutor().submit(new ServiceTask<Void>() {
            @Override
            protected Void call() throws Exception {
                recordManager.startOngoingVisit(OngoingVisit.builder()
                    .studentId(selectedStudentId)
                    .startTime(Instant.now()).build());
                Platform.runLater(() -> {
                    studentSelector.textProperty().set("");
                });
                return null;
            }
        });
    }

    public void onEndOngoingVisit(Integer studentId) {
        LiveVisitModel endedVisit = ongoingVisitsModel.get(studentId);
        Instant startTime = endedVisit.getStartTime().get();
        ThreadManager.mainBackgroundExecutor().submit(new ServiceTask<Void>() {
            @Override
            protected Void call() throws Exception {
                recordManager.endOngoingVisit(OngoingVisit.builder()
                    .studentId(studentId)
                    .startTime(startTime).build(),
                    (int) ChronoUnit.MINUTES.between(startTime, Instant.now()));
                return null;
            }
        });
    }

}
