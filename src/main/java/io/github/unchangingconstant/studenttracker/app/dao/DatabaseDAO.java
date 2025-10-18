package io.github.unchangingconstant.studenttracker.app.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.jdbi.v3.sqlobject.config.KeyColumn;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import io.github.unchangingconstant.studenttracker.app.entities.Student;
import io.github.unchangingconstant.studenttracker.app.entities.Visit;
import io.github.unchangingconstant.studenttracker.app.mappers.RowToStudentMapper;

// Read up on mappers, section 7 of JDBI docs
public interface DatabaseDAO {

    @SqlQuery("SELECT * FROM students WHERE student_id = ?")
    @RegisterRowMapper(RowToStudentMapper.class)
    public Student getStudent(Integer studentId);

    @SqlQuery("SELECT * FROM students")
    @RegisterRowMapper(RowToStudentMapper.class)
    @KeyColumn("student_id")
    public Map<Integer, Student> getAllStudents();

    @SqlUpdate("INSERT INTO students (first_name, middle_name, last_name, subjects) VALUES (?, ?, ?, ?)")
    @GetGeneratedKeys // gets the new id of the student
    public Integer insertStudent(String firstName, String middleName, String lastName, short subjects);

    @SqlUpdate("DELETE FROM students WHERE student_id = ?")
    public boolean deleteStudent(Integer studentId);

    @SqlQuery("SELECT * FROM visits WHERE visit_id = ?")
    @RegisterFieldMapper(Visit.class)
    public Visit getVisit(Integer visitId);

    @SqlQuery("SELECT * FROM visits")
    @RegisterFieldMapper(Visit.class)
    public List<Visit> getAllVisits();

    @SqlQuery("SELECT * FROM visits WHERE end_time IS NULL")
    @RegisterFieldMapper(Visit.class)
    public List<Visit> getOngoingVisits();

    @SqlUpdate("INSERT INTO visits (start_time, end_time, student_id) VALUES (?, ?, ?)")
    @GetGeneratedKeys // gets the new id of the visit
    public Integer insertVisit(LocalDateTime startTime, LocalDateTime endTime, Integer studentId);

    @SqlUpdate("DELETE FROM visits WHERE student_id = ?")
    // TODO get all of the deleted visits' ids?
    public boolean deleteStudentVisits(Integer studentId);

    @SqlUpdate("UPDATE visits SET end_time = ? WHERE visit_id = ?")
    // TODO jesus christ write your tests man, your services count on it
    public void updateVisitEndtime(LocalDateTime endTime, Integer visitId);

}
