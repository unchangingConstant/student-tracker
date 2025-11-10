package io.github.unchangingconstant.studenttracker.app.gui.controllers.pages;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.custom.OngoingVisitView;
import io.github.unchangingconstant.studenttracker.app.gui.custom.StudentSelector;
import io.github.unchangingconstant.studenttracker.app.gui.viewmodels.AttendanceDashboardViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class SessionPageController implements Controller {

    @FXML
    private OngoingVisitView ongoingVisitsView;
    @FXML
    private StudentSelector studentSelector; 
    @FXML
    private Button startVisitButton;
    @FXML
    private Button menuButton;

    private ContextMenu menuPopdown = new ContextMenu();

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
            Student selected = studentSelector.selectedProperty().getValue();
            if (selected != null)  {
                viewModel.onStartVisitAction();
            }
        });
        
        MenuItem newItem = new MenuItem("Item!");
        menuPopdown.getItems().add(newItem);
        
        // menuButton.setOnAction(actionEvent ->   {
        //     menuPopdown.show(menuButton, Side.BOTTOM, 0, 0);
        // });
        viewModel.getStudentSelectorInput().bindBidirectional(studentSelector.textProperty());
        viewModel.getSelected().bind(studentSelector.selectedProperty());
        ongoingVisitsView.setOnButtonAction(studentId -> viewModel.onEndOngoingVisit(studentId));
    }

}
