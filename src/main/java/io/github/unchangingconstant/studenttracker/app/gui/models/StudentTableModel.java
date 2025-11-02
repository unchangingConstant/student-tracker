package io.github.unchangingconstant.studenttracker.app.gui.models;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.backend.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.backend.services.AttendanceService.Observer;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * Should purely represent the state of the current database
 */
@Singleton
public class StudentTableModel {

    private AttendanceService attendanceService;

    private SimpleListProperty<Student> students;

    @Inject
    public StudentTableModel(AttendanceService attendanceService) {
        Collection<Student> initialData = attendanceService.getAllStudents().values();
        this.students = new SimpleListProperty<Student>(FXCollections.observableArrayList());
        this.students.addAll(initialData);
        // Ensures model state is synced to database at all times
        Observer<Integer, Student> observer = attendanceService.getStudentsObserver();
        observer.subscribeToDeletes(studentId -> this.onDeleteStudent(studentId));
        observer.subscribeToInserts(studentId -> this.onInsertStudent(studentId));
        observer.subscribeToUpdates(student -> this.onUpdateStudent(student));

        this.attendanceService = attendanceService;
    }

    public void bind(Property<ObservableList<Student>> property) {
        property.bind(this.students);
    }

    public ObservableList<Student> getStudents()   {
        return FXCollections.unmodifiableObservableList(students.get());
    }

    private void onInsertStudent(Integer studentId) {
        students.add(attendanceService.getStudent(studentId));
    }

    private void onDeleteStudent(Integer studentId) {
        students.removeIf(student -> student.getStudentId().equals(studentId));
    }

    private void onUpdateStudent(Student student) {
        students.add(student);
    }

}
