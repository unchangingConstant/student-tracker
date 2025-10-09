package io.github.unchangingconstant.studenttracker.app.viewmodels;

import lombok.Getter;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.entities.Student;
import io.github.unchangingconstant.studenttracker.app.models.StudentsTableModel;
import io.github.unchangingconstant.studenttracker.app.services.StudentsTableService;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;

public class RootViewModel {

    private StudentsTableModel model;
    private StudentsTableService dbAccess;

    @Getter
    private SimpleStringProperty firstNameInput;
    @Getter
    private SimpleStringProperty lastNameInput;
    @Getter
    private SimpleStringProperty middleNameInput;
    @Getter
    private SimpleListProperty<Student> studentList;

    @Inject
    public RootViewModel(StudentsTableModel model, StudentsTableService dbAccess) {
        // Dependencies
        this.model = model;
        this.dbAccess = dbAccess;
        // ViewModel properties
        this.firstNameInput = new SimpleStringProperty();
        this.lastNameInput = new SimpleStringProperty();
        this.middleNameInput = new SimpleStringProperty();
        this.studentList = new SimpleListProperty<Student>();
        // Bind viewmodel to model
        this.model.bind(studentList);
    }

    public void onRegisterButtonAction() {
        dbAccess.insertStudent(firstNameInput.getValue(), lastNameInput.getValue(),
                middleNameInput.getValue().equals("") ? null : middleNameInput.getValue(), (short) 1);
    }

    public void onDeleteButtonAction() {
        throw new UnsupportedOperationException();
    }

}