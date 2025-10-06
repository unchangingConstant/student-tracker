package io.github.unchangingconstant.studenttracker.ui.viewmodels;

import io.github.unchangingconstant.studenttracker.app.domainentities.Student;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import io.github.unchangingconstant.studenttracker.app.models.impl.AttendanceDatabaseModel;

public class RegisterViewModel {

    // Model
    private AttendanceDatabaseModel model;

    // UI State
    @Getter
    private SimpleStringProperty firstNameInput;
    @Getter
    private SimpleStringProperty lastNameInput;
    @Getter
    private SimpleStringProperty middleNameInput;

    public void onRegisterButtonAction() {
        Student student = new Student(firstNameInput.getValue(), lastNameInput.getValue(),
                middleNameInput.getValue().equals("") ? null : middleNameInput.getValue(), (short) 1, null);
        model.addStudent(student);
    }

    public void onDeleteButtonAction() {
        throw new UnsupportedOperationException();
    }

    public void setModel(AttendanceDatabaseModel model) {
        this.model = model;
    }

}
