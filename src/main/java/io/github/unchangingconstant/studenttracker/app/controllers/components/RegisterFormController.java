package io.github.unchangingconstant.studenttracker.app.controllers.components;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.viewmodels.StudentTableViewModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class RegisterFormController implements Controller {

    @FXML
    private TextField firstNameInput;
    @FXML
    private TextField middleNameInput;
    @FXML
    private TextField lastNameInput;
    @FXML
    private ComboBox<String> subjectsInput;
    @FXML
    private Button registerButton;

    private StudentTableViewModel viewModel;

    @Inject
    public RegisterFormController(StudentTableViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize() {
        bindRegisterForm();
        bindRegisterButton();
    }

    private void bindRegisterButton() {
        registerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                viewModel.onRegisterButtonAction();
            }
        });
    }

    private void bindRegisterForm() {
        viewModel.getFirstNameInput().bindBidirectional(firstNameInput.textProperty());
        viewModel.getMiddleNameInput().bindBidirectional(middleNameInput.textProperty());
        viewModel.getLastNameInput().bindBidirectional(lastNameInput.textProperty());
        viewModel.getSubjectsInput().bindBidirectional(subjectsInput.getEditor().textProperty());
    }

}
