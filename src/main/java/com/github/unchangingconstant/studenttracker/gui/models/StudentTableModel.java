package com.github.unchangingconstant.studenttracker.gui.models;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import com.github.unchangingconstant.studenttracker.app.domain.Student;
import com.github.unchangingconstant.studenttracker.app.mappers.model.DomainToStudentModelMapper;
import com.github.unchangingconstant.studenttracker.app.services.AttendanceObserver;
import com.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.application.Platform;
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

    private SimpleListProperty<StudentModel> students;

    @Inject
    public StudentTableModel(AttendanceService attendanceService) {
        /**
         * Yes, this stays on the JavaFX thread. This model is unusable until the following code runs.
         * For this reason I have made all Service methods synchronized
         */
        Collection<Student> initialData = attendanceService.getAllStudents().values();
        this.students = new SimpleListProperty<StudentModel>(FXCollections.observableArrayList());
        initialData.forEach(domain -> this.students.add(DomainToStudentModelMapper.map(domain)));
        // Ensures model state is synced to database at all times
        AttendanceObserver<Student> observer = attendanceService.getStudentsObserver();

        /**
         * These Runnables will be called from the background thread and potentially
         * affect the JavaFX thread. So, Platform.runLater() is necessary here.
         */
        observer.subscribeToDeletes(students -> Platform.runLater(() -> this.onDeleteStudent(students)));
        observer.subscribeToInserts(students -> Platform.runLater(() -> this.onInsertStudent(students)));
        observer.subscribeToUpdates(students -> Platform.runLater(() -> this.onUpdateStudent(students)));

        this.attendanceService = attendanceService;
    }

    public void bindProperty(Property<ObservableList<StudentModel>> property) {
        property.bind(this.students);
    }

    public ObservableList<StudentModel> getStudents()   {
        return FXCollections.unmodifiableObservableList(students.get());
    }

    public StudentModel getStudent(Integer studentId) {
        for (StudentModel student: students) {
            if (student.getStudentId().get().equals(studentId)) {
                return student;
            }
        }
        throw new NoSuchElementException("Student with studentId " + String.valueOf(studentId) + " not found");
    }

    private void onInsertStudent(List<Student> insertedStudents) {
        insertedStudents.forEach(student -> {
            students.add(DomainToStudentModelMapper.map(student));
        });
    }

    private void onDeleteStudent(List<Student> deletedStudents) {
        deletedStudents.forEach(student -> {
            students.removeIf(studentModel -> studentModel.getStudentId().get() == student.getStudentId());
        });
    }

    private void onUpdateStudent(List<Student> updatedStudents) {
        updatedStudents.forEach(updatedStudent -> {
            for (StudentModel student: students) {
                if (student.getStudentId().get() == updatedStudent.getStudentId()) {
                    student.getFullLegalName().set(updatedStudent.getFullLegalName());
                    student.getPrefName().set(updatedStudent.getPrefName());
                    student.getSubjects().set(updatedStudent.getSubjects());
                    break;
                }
            }
        });
        // TODO throw exception if loop exits with nothing??
    }

}
