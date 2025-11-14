package io.github.unchangingconstant.studenttracker.app.controllers.pages;

import java.io.IOException;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.StudentTrackerApp;
import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.custom.OngoingVisitView;
import io.github.unchangingconstant.studenttracker.app.custom.StudentSelector;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import io.github.unchangingconstant.studenttracker.app.viewmodels.AttendanceDashboardViewModel;
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

    private AttendanceDashboardViewModel viewModel;

    @Inject
    public SessionPageController(AttendanceDashboardViewModel viewModel)  {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize() {
        viewModel.bindToOngoingVisitsModel(ongoingVisitsView.itemsProperty());
        viewModel.bindToStudentsModel(studentSelector.optionsProperty());
        startVisitButton.setOnAction(actionEvent -> {
            StudentModel selected = studentSelector.selectedProperty().getValue();
            if (selected != null)  {
                viewModel.onStartVisitAction();
            }
        });
        
        // menuButton.setOnAction(actionEvent ->   {
        //     menuPopdown.show(menuButton, Side.BOTTOM, 0, 0);
        // });
        viewModel.getStudentSelectorInput().bindBidirectional(studentSelector.textProperty());
        viewModel.getSelected().bind(studentSelector.selectedProperty());
        ongoingVisitsView.setOnButtonAction(studentId -> viewModel.onEndOngoingVisit(studentId));

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

}
