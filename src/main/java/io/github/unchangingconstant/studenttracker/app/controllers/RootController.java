package io.github.unchangingconstant.studenttracker.app.controllers;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.entities.domain.Student;
import io.github.unchangingconstant.studenttracker.app.viewmodels.RootViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class RootController implements Controller {

    @FXML
    private ListView<Student> databaseView;
    @FXML
    private TextField firstNameInput;
    @FXML
    private TextField middleNameInput;
    @FXML
    private TextField lastNameInput;

    private RootViewModel viewmodel;

    @Inject
    public RootController(RootViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }

    @Override
    public void initialize() {
        viewmodel.getFirstNameInput().bind(firstNameInput.textProperty());
        viewmodel.getLastNameInput().bind(lastNameInput.textProperty());
        viewmodel.getMiddleNameInput().bind(middleNameInput.textProperty());
        // ViewModel not changeable by view in this binding order
        databaseView.itemsProperty().bind(viewmodel.getStudentList());
    }

    public void onRegisterButtonAction() {
        viewmodel.onRegisterButtonAction();
    }

}
