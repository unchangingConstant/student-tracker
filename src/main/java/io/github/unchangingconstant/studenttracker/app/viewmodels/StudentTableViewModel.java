package io.github.unchangingconstant.studenttracker.app.viewmodels;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.entities.Student;
import io.github.unchangingconstant.studenttracker.app.models.StudentTableModel;
import io.github.unchangingconstant.studenttracker.app.services.StudentService;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

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
                (short) 1);
        firstNameInput.set("");
        middleNameInput.set("");
        lastNameInput.set("");
        subjectsInput.set("");
    }

    public void bindToModelProperty(ObjectProperty<ObservableList<Student>> map) {
        model.bind(map);
    }

}
