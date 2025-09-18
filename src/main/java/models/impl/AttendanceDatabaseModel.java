package models.impl;

import java.util.List;
import java.util.NoSuchElementException;

import dao.AttendanceDAO;
import domainentities.Student;
import models.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AttendanceDatabaseModel implements Model<ObjectProperty<ObservableList<Student>>> {

    private SimpleListProperty<Student> students;
    // private AttendanceDatabaseAccess database;

    public AttendanceDatabaseModel(AttendanceDAO database) {
        List<Student> initialData = database.getAllStudents();
        // This way. Otherwise the property has a null list
        this.students = new SimpleListProperty<Student>(FXCollections.observableArrayList());
        // initialData.forEach(row -> students.add(mapper.map(row)));
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void deleteStudent(Integer id) {
        // TODO At some point, we also need some data validation to ensure consistent
        // sync between the database and the model
        // TODO implement student list as a set that uses the id as a key. None of this
        // uncertain business
        boolean removed = students.removeIf(student -> student.getStudentId().equals(id));
        if (!removed)
            throw new NoSuchElementException("Student with id is not in the model");
    }

    public int lastId() {
        return students.get(students.size() - 1).getStudentId();
    }

    @Override
    public void bind(ObjectProperty<ObservableList<Student>> property) {
        property.bind(students);
    }

}
