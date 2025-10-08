package io.github.unchangingconstant.studenttracker.ui.viewmodels;

import lombok.Getter;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.entities.Student;
import io.github.unchangingconstant.studenttracker.app.models.StudentsTableModel;
import io.github.unchangingconstant.studenttracker.app.services.DatabaseAccessService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RootViewModel {

    private StudentsTableModel model;
    private DatabaseAccessService dbAccess;

    @Getter
    private SimpleStringProperty firstNameInput;
    @Getter
    private SimpleStringProperty lastNameInput;
    @Getter
    private SimpleStringProperty middleNameInput;
    @Getter
    private Property<ObservableList<Student>> studentList;

    @Inject
    public RootViewModel(StudentsTableModel model, DatabaseAccessService dbAccess) {
        // Dependencies
        this.model = model;
        this.dbAccess = dbAccess;
        // ViewModel properties
        this.firstNameInput = new SimpleStringProperty();
        this.lastNameInput = new SimpleStringProperty();
        this.middleNameInput = new SimpleStringProperty();
        this.studentList = new SimpleListProperty<Student>();
        // Bind model to ViewModel
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