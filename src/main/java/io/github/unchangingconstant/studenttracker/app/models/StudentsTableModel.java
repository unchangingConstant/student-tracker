package io.github.unchangingconstant.studenttracker.app.models;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.services.StudentsTableService;
import io.github.unchangingconstant.studenttracker.app.entities.Student;
import io.github.unchangingconstant.studenttracker.app.services.StudentsTableEventService;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;

@Singleton
public class StudentsTableModel {

    private StudentsTableService dbAccess;

    private SimpleMapProperty<Integer, Student> students;

    @Inject
    public StudentsTableModel(StudentsTableService dbAccess, StudentsTableEventService eventService) {
        Map<Integer, Student> initialData = dbAccess.getAllStudents();
        // TODO populate this!!!
        this.students = new SimpleMapProperty<Integer, Student>();
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
        Student newStudent = this.dbAccess.getStudent(studentId);
        students.put(newStudent.getStudentId(), newStudent);
    }

    private void onDeleteStudent(Integer studentId) {
        // TODO figure out something better lol
    }

    private void onUpdateStudent(Student student) {

    }

}
