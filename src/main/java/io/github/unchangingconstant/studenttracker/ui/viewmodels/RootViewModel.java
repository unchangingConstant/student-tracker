package io.github.unchangingconstant.studenttracker.ui.viewmodels;

import lombok.Getter;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.domainentities.Student;
import io.github.unchangingconstant.studenttracker.app.models.impl.AttendanceDatabaseModel;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RootViewModel {

    private AttendanceDatabaseModel model;

    @Getter
    private SimpleStringProperty firstNameInput;
    @Getter
    private SimpleStringProperty lastNameInput;
    @Getter
    private SimpleStringProperty middleNameInput;
    @Getter
    private SimpleListProperty<Student> studentList;

    @Inject
    public RootViewModel(AttendanceDatabaseModel model) {
        this.firstNameInput = new SimpleStringProperty();
        this.lastNameInput = new SimpleStringProperty();
        this.middleNameInput = new SimpleStringProperty();
        // TODO Make this an exposed model property instead (MODEL REFACTOR!!!)
        this.studentList = new SimpleListProperty<Student>(FXCollections.observableArrayList(model.getAllStudents()));
        this.model = model;
    }

    public void onRegisterButtonAction() {
        Student student = new Student(firstNameInput.getValue(), lastNameInput.getValue(),
                middleNameInput.getValue().equals("") ? null : middleNameInput.getValue(), (short) 1, null);
        model.addStudent(student);
        studentList.add(student);
    }

    public void onDeleteButtonAction() {
        throw new UnsupportedOperationException();
    }

}