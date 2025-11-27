package io.github.unchangingconstant.studenttracker.app.controllers.pages;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.controllers.components.LiveAttendanceView;
import io.github.unchangingconstant.studenttracker.app.controllers.components.StudentSelector;
import io.github.unchangingconstant.studenttracker.app.models.OngoingVisitModel;
import io.github.unchangingconstant.studenttracker.app.models.OngoingVisitTableModel;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import io.github.unchangingconstant.studenttracker.app.models.StudentTableModel;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AttendanceDashboardPageController implements Controller {

    @FXML
    private LiveAttendanceView liveAttendanceView;
    @FXML
    private StudentSelector studentSelector; 
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
        ongoingVisitsModel.bindProperty(liveAttendanceView.itemsProperty());
        studentTableModel.bindProperty(studentSelector.optionsProperty());
        startVisitButton.setOnAction(actionEvent -> {
            StudentModel selected = studentSelector.selectedProperty().getValue();
            if (selected != null)  {
                onStartVisitAction(selected);
            }
        });
        liveAttendanceView.setOnButtonAction(studentId -> onEndOngoingVisit(studentId));

    }

    public void onStartVisitAction(StudentModel selectedStudent) {
        attendanceService.startOngoingVisit(selectedStudent.getStudentId().get());
        studentSelector.textProperty().set("");
    }

    public void onEndOngoingVisit(Integer studentId) {
        OngoingVisitModel endedVisit = ongoingVisitsModel.get(studentId);
        attendanceService.endOngoingVisit(endedVisit.getStudentId().get(), endedVisit.getStartTime().get());
    }

}
