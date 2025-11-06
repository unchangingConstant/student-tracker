package io.github.unchangingconstant.studenttracker.app.gui.controllers.pages;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.viewmodels.DatabaseViewModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class RegisterPageController implements Controller {

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
    @FXML
    private TableView<Student> studentView;

    private DatabaseViewModel viewModel;

    @Override
    public void initialize() {
        registerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                viewModel.onRegisterButtonAction();
            }
        });
        viewModel.bindToModelProperty(studentView.itemsProperty());
        viewModel.getFirstNameInput().bindBidirectional(firstNameInput.textProperty());
        viewModel.getMiddleNameInput().bindBidirectional(middleNameInput.textProperty());
        viewModel.getLastNameInput().bindBidirectional(lastNameInput.textProperty());
        viewModel.getSubjectsInput().bindBidirectional(subjectsInput.getEditor().textProperty());
    }
}
