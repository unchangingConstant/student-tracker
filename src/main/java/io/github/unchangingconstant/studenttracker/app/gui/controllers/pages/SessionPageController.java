package io.github.unchangingconstant.studenttracker.app.gui.controllers.pages;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.components.OngoingVisitView;
import io.github.unchangingconstant.studenttracker.app.gui.components.StudentSelector;
import io.github.unchangingconstant.studenttracker.app.gui.viewmodels.SessionViewModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
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
        
        MenuItem newItem = new MenuItem("Item!");
        menuPopdown.getItems().add(newItem);
        
        // menuButton.setOnAction(actionEvent ->   {
        //     menuPopdown.show(menuButton, Side.BOTTOM, 0, 0);
        // });
        viewModel.getStudentSelectorInput().bindBidirectional(studentSelector.textProperty());
        viewModel.getSelected().bindBidirectional(studentSelector.selectedProperty());
        ongoingVisitsView.setOnButtonAction(studentId -> viewModel.onEndOngoingVisit(studentId));
    }

}
