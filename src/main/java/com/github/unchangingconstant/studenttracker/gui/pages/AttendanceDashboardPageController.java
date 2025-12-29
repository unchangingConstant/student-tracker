package com.github.unchangingconstant.studenttracker.gui.pages;

import java.time.Instant;

import com.github.unchangingconstant.studenttracker.StudentTrackerApp;
import com.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import com.github.unchangingconstant.studenttracker.gui.Controller;
import com.github.unchangingconstant.studenttracker.gui.WindowController;
import com.github.unchangingconstant.studenttracker.gui.components.LiveAttendanceView;
import com.github.unchangingconstant.studenttracker.gui.components.StudentSelector;
import com.github.unchangingconstant.studenttracker.gui.models.OngoingVisitModel;
import com.github.unchangingconstant.studenttracker.gui.models.OngoingVisitTableModel;
import com.github.unchangingconstant.studenttracker.gui.models.StudentModel;
import com.github.unchangingconstant.studenttracker.gui.models.StudentTableModel;
import com.github.unchangingconstant.studenttracker.gui.taskutils.ServiceTask;
import com.github.unchangingconstant.studenttracker.threads.ThreadManager;
import com.google.inject.Inject;

import javafx.application.Platform;
import javafx.concurrent.Task;
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

    private OngoingVisitTableModel ongoingVisitsModel;
    private StudentTableModel studentTableModel;
    private AttendanceService attendanceService;
    private WindowController windowController;

    @Inject
    public AttendanceDashboardPageController(
        OngoingVisitTableModel ongoingVisitsModel, 
        StudentTableModel studentTableModel, 
        AttendanceService attendanceService, 
        WindowController windowController)  {
        this.ongoingVisitsModel = ongoingVisitsModel;
        this.studentTableModel = studentTableModel;
        this.attendanceService = attendanceService;
        this.windowController = windowController;
    }

    @Override
    public void initialize() {
        ongoingVisitsModel.bindProperty(liveAttendanceView.itemsProperty());
        studentTableModel.bindProperty(studentSelector.optionsProperty());
        startVisitButton.setOnAction(actionEvent -> {
            StudentModel selected = studentSelector.selectedProperty().getValue();
            if (selected != null)  {
                onStartVisitAction(selected);
            }
        });
        liveAttendanceView.setOnButtonAction(studentId -> onEndOngoingVisit(studentId));
        recordManagerMenuItem.setOnAction(actionEvent -> windowController.openRecordManager());
    }

    public void onStartVisitAction(StudentModel selectedStudent) {
        Integer selectedStudentId = selectedStudent.getStudentId().get();
        ThreadManager.mainBackgroundExecutor().submit(new ServiceTask<Void>() {
            @Override
            protected Void call() throws Exception {
                attendanceService.startOngoingVisit(selectedStudentId);
                Platform.runLater(() -> {
                    studentSelector.textProperty().set("");
                });
                return null;
            }
        });
    }

    public void onEndOngoingVisit(Integer studentId) {
        OngoingVisitModel endedVisit = ongoingVisitsModel.get(studentId);
        Instant startTime = endedVisit.getStartTime().get();
        ThreadManager.mainBackgroundExecutor().submit(new ServiceTask<Void>() {
            @Override
            protected Void call() throws Exception {
                attendanceService.endOngoingVisit(studentId, startTime);
                return null;
            }
        });
    }

}
