package io.github.unchangingconstant.studenttracker.ui.controllers.impl;

import java.net.URL;
import java.util.ResourceBundle;

import io.github.unchangingconstant.studenttracker.app.domainentities.Student;
import io.github.unchangingconstant.studenttracker.ui.controllers.Controller;
import io.github.unchangingconstant.studenttracker.ui.viewmodels.RootViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class RootController implements Controller {

    @FXML
    private ListView<Student> databasePage;
    @FXML
    private TextField firstNameInput;
    @FXML
    private TextField middleNameInput;
    @FXML
    private TextField lastNameInput;

    private RootViewModel viewmodel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewmodel.getStudentList().bind(databasePage.itemsProperty());
        viewmodel.getFirstNameInput().bind(firstNameInput.textProperty());
        viewmodel.getLastNameInput().bind(lastNameInput.textProperty());
        viewmodel.getMiddleNameInput().bind(middleNameInput.textProperty());
    }

    public void onRegisterButtonAction() {
        viewmodel.onRegisterButtonAction();
    }

}
