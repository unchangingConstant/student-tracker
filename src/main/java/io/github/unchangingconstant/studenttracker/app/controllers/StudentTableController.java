package io.github.unchangingconstant.studenttracker.app.controllers;

import java.util.List;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.entities.Student;
import io.github.unchangingconstant.studenttracker.app.viewmodels.StudentTableViewModel;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class StudentTableController implements Controller {

    @FXML
    private TableView<Student> studentView;
    @FXML
    private TextField firstNameInput;
    @FXML
    private TextField middleNameInput;
    @FXML
    private TextField lastNameInput;
    @FXML
    private TextField subjectsInput;
    @FXML
    private Button registerButton;

    private StudentTableViewModel viewModel;

    @Inject
    public StudentTableController(StudentTableViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize() {
        viewModel.bindToModelProperty(studentView.itemsProperty());
        populateTable();
        bindRegisterForm();
        bindRegisterButton();
    }

    private void populateTable() {
        TableColumn<Student, String> nameColumn = new TableColumn<>("Student Name");
        nameColumn.setCellValueFactory(row -> {
            Student s = row.getValue();
            return new SimpleStringProperty(String.format("%s%s %s", s.getFirstName(),
                    s.getMiddleName() == null ? "" : (" " + s.getMiddleName()),
                    s.getLastName()));
        });
        studentView.getColumns().add(nameColumn);
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
        viewModel.getSubjectsInput().bindBidirectional(subjectsInput.textProperty());
    }

}
