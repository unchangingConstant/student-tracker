package viewmodels.impl;

import domainentities.Student;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import models.impl.AttendanceDatabaseModel;
import viewmodels.ViewModel;

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
