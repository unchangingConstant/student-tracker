package io.github.unchangingconstant.studenttracker.app.controllers.pages;

import java.io.IOException;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.StudentTrackerApp;
import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.custom.OngoingVisitView;
import io.github.unchangingconstant.studenttracker.app.custom.StudentSelector;
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

public class SessionPageController implements Controller {

    @FXML
    private OngoingVisitView ongoingVisitsView;
    @FXML
    private StudentSelector studentSelector; 
    @FXML
    private Button startVisitButton;
    @FXML
    private Button menuButton;
    @FXML
    private MenuItem recordManagerMenuItem;

    private OngoingVisitTableModel ongoingVisitsModel;
    private StudentTableModel studentTableModel;
    private AttendanceService attendanceService;

    @Inject
    public SessionPageController(OngoingVisitTableModel ongoingVisitsModel, StudentTableModel studentTableModel, AttendanceService attendanceService)  {
        this.ongoingVisitsModel = ongoingVisitsModel;
        this.studentTableModel = studentTableModel;
        this.attendanceService = attendanceService;
    }

    @Override
    public void initialize() {
        ongoingVisitsModel.bindProperty(ongoingVisitsView.itemsProperty());
        studentTableModel.bindProperty(studentSelector.optionsProperty());
        startVisitButton.setOnAction(actionEvent -> {
            StudentModel selected = studentSelector.selectedProperty().getValue();
            if (selected != null)  {
                onStartVisitAction(selected);
            }
        });
        
        // menuButton.setOnAction(actionEvent ->   {
        //     menuPopdown.show(menuButton, Side.BOTTOM, 0, 0);
        // });
        ongoingVisitsView.setOnButtonAction(studentId -> onEndOngoingVisit(studentId));

        recordManagerMenuItem.setOnAction(actionEvent -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/pages/database_manager_page.fxml"));
                fxmlLoader.setControllerFactory(StudentTrackerApp.appContext::getInstance);
                Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                Stage stage = new Stage();
                stage.setTitle("Student Tracker Record Manager");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void onStartVisitAction(StudentModel selectedStudent) {
        attendanceService.startOngoingVisit(selectedStudent.getStudentId().get());
        studentSelector.textProperty().set("");
    }

    public void onEndOngoingVisit(Integer studentId) {
        attendanceService.endOngoingVisit(ongoingVisitsModel.get(studentId));
    }

}
