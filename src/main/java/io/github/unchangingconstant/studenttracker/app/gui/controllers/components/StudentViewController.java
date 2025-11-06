package io.github.unchangingconstant.studenttracker.app.gui.controllers.components;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.viewmodels.DatabaseViewModel;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StudentViewController implements Controller {

    @FXML
    private TableView<Student> studentView;

    private DatabaseViewModel viewModel;

    @Inject
    public StudentViewController(DatabaseViewModel viewModel) {
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
            Instant dateAdded = row.getValue().getDateAdded();
            return new SimpleStringProperty(formatter.format(dateAdded));
        });
        studentView.getColumns().add(nameColumn);
        studentView.getColumns().add(subjectColumn);
        studentView.getColumns().add(dateColumn);
    }

}
