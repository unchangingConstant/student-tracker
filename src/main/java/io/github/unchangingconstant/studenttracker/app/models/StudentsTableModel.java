package io.github.unchangingconstant.studenttracker.app.models;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.entities.Student;
import io.github.unchangingconstant.studenttracker.app.services.StudentsTableService;
import io.github.unchangingconstant.studenttracker.app.services.StudentsTableEventService;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

@Singleton
public class StudentsTableModel {

    private StudentsTableService dbAccess;

    private SimpleListProperty<Student> students;

    @Inject
    public StudentsTableModel(StudentsTableService dbAccess, StudentsTableEventService eventService) {
        List<Student> initialData = dbAccess.getAllStudents();
        this.students = new SimpleListProperty<Student>(FXCollections.observableArrayList(initialData));
        // Ensures model is state is synced to database at all times
        eventService.subscribeToDeletes(studentId -> this.onDeleteStudent(studentId));
        eventService.subscribeToInserts(studentId -> this.onInsertStudent(studentId));
        eventService.subscribeToUpdates(student -> this.onUpdateStudent(student));
        this.dbAccess = dbAccess;
    }

    private void onInsertStudent(Integer studentId) {
        students.add(dbAccess.getStudent(studentId));
    }

    private void onDeleteStudent(Integer studentId) {
        // TODO figure out something better lol
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                students.remove(student);
                break;
            }
        }
    }

    private void onUpdateStudent(Student student) {

    }

    public void bind(SimpleListProperty<Student> property) {
        property.bind(this.students);
    }

}
