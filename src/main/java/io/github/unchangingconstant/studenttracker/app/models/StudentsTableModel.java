package io.github.unchangingconstant.studenttracker.app.models;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.services.StudentService;
import io.github.unchangingconstant.studenttracker.app.entities.domain.Student;
import io.github.unchangingconstant.studenttracker.app.services.StudentEventService;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;

@Singleton
public class StudentsTableModel {

    private StudentService dbAccess;

    private SimpleMapProperty<Integer, Student> students;

    @Inject
    public StudentsTableModel(StudentService dbAccess, StudentEventService eventService) {
        Map<Integer, Student> initialData = dbAccess.getAllStudents();
        // TODO populate this!!!
        this.students = new SimpleMapProperty<Integer, Student>(FXCollections.observableHashMap());
        this.students.putAll(initialData);
        // Ensures model state is synced to database at all times
        eventService.subscribeToDeletes(studentId -> this.onDeleteStudent(studentId));
        eventService.subscribeToInserts(studentId -> this.onInsertStudent(studentId));
        eventService.subscribeToUpdates(student -> this.onUpdateStudent(student));

        this.dbAccess = dbAccess;
    }

    public void bind(SimpleMapProperty<Integer, Student> property) {
        property.bind(this.students);
    }

    private void onInsertStudent(Integer studentId) {
        // TODO WRITE THIS
        // students.put(student.getStudentId(), studentId);
    }

    private void onDeleteStudent(Integer studentId) {
        // TODO figure out something better lol
    }

    private void onUpdateStudent(Student student) {

    }

}
