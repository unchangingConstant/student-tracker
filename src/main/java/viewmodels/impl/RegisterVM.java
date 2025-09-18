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
        Student student = Student.builder().firstName(firstNameInput.getText()).lastName(lastNameInput.getText())
                .middleName(middleNameInput.getText().equals("") ? null : middleNameInput.getText()).subjects((short) 1)
                .build();
        model.addStudent(student);
    }

    public void onDeleteButtonAction() {
        model.deleteStudent(model.lastId());
    }

    @Override
    public void setModel(AttendanceDatabaseModel model) {
        this.model = model;
    }

}
