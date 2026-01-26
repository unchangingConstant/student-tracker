package com.github.unchangingconstant.studenttracker.gui.models;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.github.unchangingconstant.studenttracker.app.entities.Student;
import com.github.unchangingconstant.studenttracker.app.dbmanager.AttendanceObserver;
import com.github.unchangingconstant.studenttracker.app.dbmanager.AttendanceRecordManager;
import com.github.unchangingconstant.studenttracker.gui.utils.MapToListBinding;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * Should purely represent the state of the current database
 */
@Singleton
public class StudentTableModel {

    private final AttendanceRecordManager recordManager;

    private final SimpleMapProperty<Integer, StudentModel> students;
    private final MapToListBinding<Integer, StudentModel> studentList;

    @Inject
    public StudentTableModel(AttendanceRecordManager recordManager) {
        /**
         * Yes, this stays on the JavaFX thread. This model is unusable until the following code runs.
         * For this reason I have made all Service methods synchronized
         */
        Collection<Student> initialData = recordManager.getAllStudents();
        this.students = new SimpleMapProperty<Integer, StudentModel>(FXCollections.observableHashMap());
        initialData.forEach(StudentModel::new);
        this.studentList = new MapToListBinding<>(students);

        // Ensures model state is synced to database at all times
        AttendanceObserver<Student> observer = recordManager.getStudentsObserver();
        /**
         * These Runnables will be called from the background thread and potentially
         * affect the JavaFX thread. So, Platform.runLater() is necessary here.
         */
        observer.subscribeToDeletes(students -> Platform.runLater(() -> this.onDeleteStudent(students)));
        observer.subscribeToInserts(students -> Platform.runLater(() -> this.onInsertStudent(students)));
        observer.subscribeToUpdates(students -> Platform.runLater(() -> this.onUpdateStudent(students)));

        this.recordManager = recordManager;
    }

    public void bindList(Property<ObservableList<StudentModel>> list) {
        list.bind(studentList);
    }

    public ObservableList<StudentModel> unmodifiableStudentList()   {
        return FXCollections.unmodifiableObservableList(studentList);
    }

    public StudentModel getStudent(Integer studentId) {
        return students.get(studentId);
    }

    private void onInsertStudent(List<Student> insertedStudents) {
        insertedStudents.forEach(student -> {
            students.put(student.getStudentId(), new StudentModel(student));
        });
    }

    private void onDeleteStudent(List<Student> deletedStudents) {
        deletedStudents.forEach(student -> {
            students.remove(student.getStudentId());
        });
    }

    private void onUpdateStudent(List<Student> updatedStudents) {
        updatedStudents.forEach(updatedStudent -> {
            StudentModel student = students.get(updatedStudent.getStudentId());
            student.update(updatedStudent);
        });
    }

}
