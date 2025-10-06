package io.github.unchangingconstant.studenttracker.ui.viewmodels;

import lombok.Getter;
import io.github.unchangingconstant.studenttracker.app.domainentities.Student;
import io.github.unchangingconstant.studenttracker.app.models.impl.AttendanceDatabaseModel;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;

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

    public void onRegisterButtonAction() {
        Student student = new Student(firstNameInput.getValue(), lastNameInput.getValue(),
                middleNameInput.getValue().equals("") ? null : middleNameInput.getValue(), (short) 1, null);
        model.addStudent(student);
    }

    public void onDeleteButtonAction() {
        throw new UnsupportedOperationException();
    }

}