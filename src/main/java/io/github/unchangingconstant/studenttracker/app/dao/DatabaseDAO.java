package io.github.unchangingconstant.studenttracker.app.dao;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import io.github.unchangingconstant.studenttracker.app.entities.Student;

// Read up on mappers, section 7 of JDBI docs
public interface DatabaseDAO {

    // Probably better for this to return a Map<Integer, Student>
    @SqlQuery("SELECT * FROM students")
    @RegisterFieldMapper(Student.class)
    public List<Student> getAllStudents();

    @SqlQuery("SELECT * FROM students WHERE studentId = ?")
    @RegisterFieldMapper(Student.class)
    public Student getStudent(Integer studentId);

    @SqlUpdate("INSERT INTO students (firstName, middleName, lastName, subjects) VALUES (?, ?, ?, ?)")
    @GetGeneratedKeys // gets the new id of the student
    public Integer insertStudent(String firstName, String middleName, String lastName, short subjects);

    @SqlUpdate("DELETE FROM students WHERE studentId = ?")
    public boolean deleteStudent(Integer studentId);

}
