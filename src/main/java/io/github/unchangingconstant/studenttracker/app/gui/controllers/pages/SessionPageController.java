package io.github.unchangingconstant.studenttracker.app.gui.controllers.pages;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.components.OngoingVisitView;
import io.github.unchangingconstant.studenttracker.app.gui.components.StudentSelector;
import io.github.unchangingconstant.studenttracker.app.gui.viewmodels.SessionViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SessionPageController implements Controller {

    @FXML
    private OngoingVisitView ongoingVisitsView;
    @FXML
    private StudentSelector studentSelector; 
    @FXML
    private Button startVisitButton;

    private SessionViewModel viewModel;

    @Inject
    public SessionPageController(SessionViewModel viewModel)  {
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
        viewModel.getStudentSelectorInput().bindBidirectional(studentSelector.textProperty());
        viewModel.getSelected().bindBidirectional(studentSelector.selectedProperty());
        ongoingVisitsView.setOnButtonAction(studentId -> viewModel.onEndOngoingVisit(studentId));
    }

}
