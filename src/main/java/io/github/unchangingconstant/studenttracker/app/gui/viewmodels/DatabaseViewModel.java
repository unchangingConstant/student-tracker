package io.github.unchangingconstant.studenttracker.app.gui.viewmodels;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.backend.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.gui.models.StudentTableModel;
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
        attendanceService.deleteStudent(studentId);
    }

    public void onRegisterButtonAction() {
        attendanceService.insertStudent(firstNameInput.get(), middleNameInput.get(), lastNameInput.get(),
                1);
        firstNameInput.set("");
        middleNameInput.set("");
        lastNameInput.set("");
        subjectsInput.set("");
    }

    public void bindToModelProperty(Property<ObservableList<Student>> map) {
        model.bind(map);
    }

}
