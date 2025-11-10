package io.github.unchangingconstant.studenttracker.app.gui.controllers.pages;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.custom.FormField;
import io.github.unchangingconstant.studenttracker.app.gui.viewmodels.DatabaseViewModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class RegisterPageController implements Controller {

    @FXML
    private FormField firstNameInput;
    @FXML
    private FormField lastNameInput;
    @FXML
    private FormField middleNameInput;
    @FXML
    private FormField subjectsInput;
    @FXML
    private Button registerButton;

    private DatabaseViewModel viewModel;

    @Inject
    public RegisterPageController(DatabaseViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize() {
        registerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                viewModel.onRegisterButtonAction();
            }
        });
        viewModel.getFirstNameInput().bindBidirectional(firstNameInput.textProperty());
        viewModel.getMiddleNameInput().bindBidirectional(middleNameInput.textProperty());
        viewModel.getLastNameInput().bindBidirectional(lastNameInput.textProperty());
        viewModel.getSubjectsInput().bindBidirectional(subjectsInput.textProperty());
    }
}
