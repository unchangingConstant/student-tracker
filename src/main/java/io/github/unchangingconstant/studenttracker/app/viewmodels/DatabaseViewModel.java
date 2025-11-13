package io.github.unchangingconstant.studenttracker.app.viewmodels;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.domain.StudentDomain;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import io.github.unchangingconstant.studenttracker.app.models.StudentTableModel;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService.IllegalDatabaseOperationException;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService.InvalidDatabaseEntryException;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import lombok.Getter;

public class DatabaseViewModel {

    private StudentTableModel model;
    private AttendanceService attendanceService;

    @Getter
    private SimpleStringProperty firstNameInput = new SimpleStringProperty();
    @Getter
    private SimpleStringProperty middleNameInput = new SimpleStringProperty();
    @Getter
    private SimpleStringProperty lastNameInput = new SimpleStringProperty();
    @Getter
    private SimpleStringProperty subjectsInput = new SimpleStringProperty();

    @Inject
    public DatabaseViewModel(StudentTableModel model, AttendanceService attendanceService) {
        this.model = model;
        this.attendanceService = attendanceService;
    }

    public void onDeleteStudentButtonAction(Integer studentId) {
        try {
            attendanceService.deleteStudent(studentId);
        } catch (IllegalDatabaseOperationException e)   {
            // TODO display error message of sorts
            System.out.println(e);
        }
    }

    public void onRegisterButtonAction() {
        try {
            attendanceService.insertStudent(firstNameInput.get(), middleNameInput.get(), lastNameInput.get(),
                1);
        } catch (InvalidDatabaseEntryException e)   {
            // TODO display error message of sorts
            System.out.println(e);
        }
        firstNameInput.set("");
        middleNameInput.set("");
        lastNameInput.set("");
        subjectsInput.set("");
    }

    public void bindToModelProperty(Property<ObservableList<StudentModel>> map) {
        model.bind(map);
    }

}
