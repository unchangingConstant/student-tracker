package io.github.unchangingconstant.studenttracker.app.models;

import java.util.Collection;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.domain.StudentDomain;
import io.github.unchangingconstant.studenttracker.app.mappers.model.DomainToStudentModelMapper;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.services.Observer;
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
        Collection<StudentDomain> initialData = attendanceService.getAllStudents().values();
        this.students = new SimpleListProperty<StudentModel>(FXCollections.observableArrayList());
        initialData.forEach(domain -> this.students.add(DomainToStudentModelMapper.map(domain)));
        // Ensures model state is synced to database at all times
        Observer<Integer, StudentDomain> observer = attendanceService.getStudentsObserver();
        observer.subscribeToDeletes(studentId -> this.onDeleteStudent(studentId));
        observer.subscribeToInserts(studentId -> this.onInsertStudent(studentId));
        observer.subscribeToUpdates(student -> this.onUpdateStudent(student));

        this.attendanceService = attendanceService;
    }

    public void bindProperty(Property<ObservableList<StudentModel>> property) {
        property.bind(this.students);
    }

    public ObservableList<StudentModel> getStudents()   {
        return FXCollections.unmodifiableObservableList(students.get());
    }

    public Boolean containsStudentWithId(Integer studentId) {
        for (StudentModel student: students)    {
            if (student.getStudentId().getValue().equals(studentId)) {
                return true;
            }
        }
        return false;
    }

    private void onInsertStudent(Integer studentId) {
        students.add(DomainToStudentModelMapper.map(attendanceService.getStudent(studentId)));
    }

    private void onDeleteStudent(Integer studentId) {
        students.removeIf(student -> student.getStudentId().get() == studentId);
    }

    private void onUpdateStudent(StudentDomain student) {
        students.add(DomainToStudentModelMapper.map(student));
    }

}
