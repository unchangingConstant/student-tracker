package io.github.unchangingconstant.studenttracker.app.models;

import java.beans.EventHandler;
import java.util.List;
import java.util.function.Consumer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.entities.Student;
import io.github.unchangingconstant.studenttracker.app.services.DatabaseAccessService;
import io.github.unchangingconstant.studenttracker.app.services.DatabaseEventService;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Singleton
public class StudentsTableModel {

    private DatabaseAccessService dbAccess;

    private SimpleListProperty<Student> students;

    @Inject
    public StudentsTableModel(DatabaseAccessService dbAccess, DatabaseEventService eventService) {
        List<Student> initialData = dbAccess.getAllStudents();
        // This way. Otherwise the property has a null list
        this.students = new SimpleListProperty<Student>(FXCollections.observableArrayList(initialData));
        // Ensures model is state is synced to database at all times
        eventService.subscribeToDeletes(studentId -> this.deleteStudent(studentId));
        eventService.subscribeToInserts(studentId -> this.insertStudent(studentId));
        eventService.subscribeToUpdates(student -> this.updateStudent(student));
        this.dbAccess = dbAccess;
    }

    private void insertStudent(Integer studentId) {
        students.add(dbAccess.getStudent(studentId));
    }

    private void deleteStudent(Integer studentId) {
        // TODO figure out something better lol
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                students.remove(student);
                break;
            }
        }
    }

    private void updateStudent(Student student) {

    }

    // TODO How should we expose the model properties. Make this definitive
    public void bind(Property<ObservableList<Student>> property) {
        property.bind(this.students);
    }

}
