package io.github.unchangingconstant.studenttracker.app.controllers.components;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.entities.Student;
import io.github.unchangingconstant.studenttracker.app.viewmodels.StudentTableViewModel;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StudentViewController implements Controller {

    @FXML
    private TableView<Student> studentView;

    private StudentTableViewModel viewModel;

    @Inject
    public StudentViewController(StudentTableViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize() {
        viewModel.bindToModelProperty(studentView.itemsProperty());
        populateTable();
    }

    private void populateTable() {
        TableColumn<Student, String> nameColumn = new TableColumn<>("Student Name");
        nameColumn.setCellValueFactory(row -> {
            Student s = row.getValue();
            return new SimpleStringProperty(String.format("%s%s %s", s.getFirstName(),
                    s.getMiddleName() == null ? "" : (" " + s.getMiddleName()),
                    s.getLastName()));
        });
        TableColumn<Student, Number> subjectColumn = new TableColumn<>("Subjects");
        subjectColumn.setCellValueFactory(row -> {
            Student s = row.getValue();
            return new SimpleIntegerProperty(s.getSubjects());
        });
        TableColumn<Student, String> dateColumn = new TableColumn<>("Date Added");
        dateColumn.setCellValueFactory(row -> {
            Student s = row.getValue();
            return new SimpleStringProperty(s.getDateAdded().toString());
        });
        studentView.getColumns().add(nameColumn);
        studentView.getColumns().add(subjectColumn);
        studentView.getColumns().add(dateColumn);
    }

}
