package io.github.unchangingconstant.studenttracker.app.models.impl;

import java.util.List;

import io.github.unchangingconstant.studenttracker.app.dao.AttendanceDAO;
import io.github.unchangingconstant.studenttracker.app.domainentities.Student;
import io.github.unchangingconstant.studenttracker.app.models.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AttendanceDatabaseModel implements Model<ObjectProperty<ObservableList<Student>>> {

    private SimpleListProperty<Student> students;
    private AttendanceDAO dao;

    public AttendanceDatabaseModel(AttendanceDAO dao) {
        List<Student> initialData = dao.getAllStudents();
        // This way. Otherwise the property has a null list
        this.students = new SimpleListProperty<Student>(FXCollections.observableArrayList(initialData));
        this.dao = dao;
    }

    // assumes all students have null studentIds when passed
    public void addStudent(Student student) {
        Integer newStudentId = dao.insertStudent(student);
        student.setStudentId(newStudentId);
        students.add(student);
    }

    public void deleteStudent(Integer studentId) {
        // TODO At some point, we also need some data validation to ensure consistent
        // sync between the database and the model
        // TODO implement student list as a set that uses the id as a key. None of this
        // uncertain business
        boolean removed = students.removeIf(student -> student.getStudentId().equals(studentId));
        if (removed)
            dao.deleteStudent(studentId);

    }

    @Override
    public void bind(ObjectProperty<ObservableList<Student>> property) {
        property.bind(students);
    }

}
