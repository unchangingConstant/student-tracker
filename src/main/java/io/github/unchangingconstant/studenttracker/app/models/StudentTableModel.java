package io.github.unchangingconstant.studenttracker.app.models;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.services.StudentService;
import io.github.unchangingconstant.studenttracker.app.entities.Student;
import io.github.unchangingconstant.studenttracker.app.services.StudentEventService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * Should purely represent the state of the current database
 */
@Singleton
public class StudentTableModel {

    private StudentService dbAccess;

    private SimpleListProperty<Student> students;

    @Inject
    public StudentTableModel(StudentService dbAccess, StudentEventService eventService) {
        Collection<Student> initialData = dbAccess.getAllStudents().values();
        this.students = new SimpleListProperty<Student>(FXCollections.observableArrayList());
        this.students.addAll(initialData);
        // Ensures model state is synced to database at all times
        eventService.subscribeToDeletes(studentId -> this.onDeleteStudent(studentId));
        eventService.subscribeToInserts(studentId -> this.onInsertStudent(studentId));
        eventService.subscribeToUpdates(student -> this.onUpdateStudent(student));

        this.dbAccess = dbAccess;
    }

    public void bind(ObjectProperty<ObservableList<Student>> property) {
        property.bind(this.students);
    }

    private void onInsertStudent(Integer studentId) {
        students.add(dbAccess.getStudent(studentId));
    }

    private void onDeleteStudent(Integer studentId) {
        students.removeIf(student -> student.getStudentId().equals(studentId));
    }

    private void onUpdateStudent(Student student) {
        students.add(student);
    }

}
