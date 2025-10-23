package io.github.unchangingconstant.studenttracker.app.gui.viewmodels;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.backend.services.StudentService;
import io.github.unchangingconstant.studenttracker.app.gui.models.StudentTableModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import lombok.Getter;

public class StudentTableViewModel {

    private StudentTableModel model;
    private StudentService studentService;

    @Getter
    private SimpleStringProperty firstNameInput = new SimpleStringProperty();
    @Getter
    private SimpleStringProperty middleNameInput = new SimpleStringProperty();
    @Getter
    private SimpleStringProperty lastNameInput = new SimpleStringProperty();
    @Getter
    private SimpleStringProperty subjectsInput = new SimpleStringProperty();

    @Inject
    public StudentTableViewModel(StudentTableModel model, StudentService studentService) {
        this.model = model;
        this.studentService = studentService;
    }

    public void onDeleteStudentButtonAction(Integer studentId) {
        studentService.deleteStudent(studentId);
    }

    public void onRegisterButtonAction() {
        studentService.insertStudent(firstNameInput.get(), middleNameInput.get(), lastNameInput.get(),
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
