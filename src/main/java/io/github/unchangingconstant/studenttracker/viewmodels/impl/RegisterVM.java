package io.github.unchangingconstant.studenttracker.viewmodels.impl;

import io.github.unchangingconstant.studenttracker.domainentities.Student;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import io.github.unchangingconstant.studenttracker.models.impl.AttendanceDatabaseModel;
import io.github.unchangingconstant.studenttracker.viewmodels.ViewModel;

public class RegisterVM implements ViewModel<AttendanceDatabaseModel> {

    private AttendanceDatabaseModel model;

    @FXML
    protected TextField firstNameInput;
    @FXML
    protected TextField middleNameInput;
    @FXML
    protected TextField lastNameInput;
    @FXML
    protected TextField subjectsInput;

    public void onRegisterButtonAction() {
        Student student = new Student(firstNameInput.getText(), lastNameInput.getText(),
                middleNameInput.getText().equals("") ? null : middleNameInput.getText(), (short) 1, null);
        model.addStudent(student);
    }

    public void onDeleteButtonAction() {
        throw new UnsupportedOperationException();
        // model.deleteStudent(100);
    }

    @Override
    public void setModel(AttendanceDatabaseModel model) {
        this.model = model;
    }

}
