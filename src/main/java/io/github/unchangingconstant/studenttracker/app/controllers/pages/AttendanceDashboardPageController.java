package io.github.unchangingconstant.studenttracker.app.controllers.pages;

import java.io.IOException;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.StudentTrackerApp;
import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.controllers.custom.ComboxStudentSelector;
import io.github.unchangingconstant.studenttracker.app.controllers.custom.OngoingVisitView;
import io.github.unchangingconstant.studenttracker.app.controllers.custom.StudentSelector;
import io.github.unchangingconstant.studenttracker.app.models.OngoingVisitTableModel;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import io.github.unchangingconstant.studenttracker.app.models.StudentTableModel;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class AttendanceDashboardPageController implements Controller {

    @FXML
    private OngoingVisitView ongoingVisitsView;
    @FXML
    private ComboxStudentSelector studentSelector; 
    @FXML
    private Button startVisitButton;
    @FXML
    private Button menuButton;

    private OngoingVisitTableModel ongoingVisitsModel;
    private StudentTableModel studentTableModel;
    private AttendanceService attendanceService;

    @Inject
    public AttendanceDashboardPageController(OngoingVisitTableModel ongoingVisitsModel, StudentTableModel studentTableModel, AttendanceService attendanceService)  {
        this.ongoingVisitsModel = ongoingVisitsModel;
        this.studentTableModel = studentTableModel;
        this.attendanceService = attendanceService;
    }

    @Override
    public void initialize() {
        ongoingVisitsModel.bindProperty(ongoingVisitsView.itemsProperty());
        studentTableModel.bindProperty(studentSelector.optionsProperty());
        startVisitButton.setOnAction(actionEvent -> {
            StudentModel selected = studentSelector.valueProperty().getValue();
            if (selected != null)  {
                onStartVisitAction(selected);
            }
        });
        ongoingVisitsView.setOnButtonAction(studentId -> onEndOngoingVisit(studentId));

    }

    public void onStartVisitAction(StudentModel selectedStudent) {
        attendanceService.startOngoingVisit(selectedStudent.getStudentId().get());
        studentSelector.textProperty().set("");
    }

    public void onEndOngoingVisit(Integer studentId) {
        attendanceService.endOngoingVisit(ongoingVisitsModel.get(studentId));
    }

}
