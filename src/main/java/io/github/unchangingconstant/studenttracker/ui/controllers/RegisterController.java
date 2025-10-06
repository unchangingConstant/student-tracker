package io.github.unchangingconstant.studenttracker.ui.controllers.impl;

import java.net.URL;
import java.util.ResourceBundle;

import io.github.unchangingconstant.studenttracker.ui.controllers.Controller;
import io.github.unchangingconstant.studenttracker.ui.viewmodels.RegisterViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class RegisterController implements Controller {

    @FXML
    private TextField firstNameInput;
    @FXML
    private TextField middleNameInput;
    @FXML
    private TextField lastNameInput;

    // private RegisterViewModel viewmodel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // viewmodel.getFirstNameInput().bind(firstNameInput.textProperty());
        // viewmodel.getLastNameInput().bind(lastNameInput.textProperty());
        // viewmodel.getMiddleNameInput().bind(middleNameInput.textProperty());
    }

    public void onRegisterButtonAction() {
        // viewmodel.onRegisterButtonAction();
    }

}
