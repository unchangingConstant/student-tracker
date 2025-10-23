package io.github.unchangingconstant.studenttracker.app.backend.dao;

import java.time.Instant;
import java.util.Map;

import org.jdbi.v3.sqlobject.config.KeyColumn;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.backend.entities.Visit;
import io.github.unchangingconstant.studenttracker.app.backend.mappers.RowToStudentMapper;
import io.github.unchangingconstant.studenttracker.app.backend.mappers.RowToVisitMapper;

// Read up on mappers, section 7 of JDBI docs
public interface DatabaseDAO {

    @SqlQuery("SELECT * FROM students WHERE student_id = ?")
    @RegisterRowMapper(RowToStudentMapper.class)
    public Student getStudent(Integer studentId);

    @SqlQuery("SELECT * FROM students")
    @RegisterRowMapper(RowToStudentMapper.class)
    @KeyColumn("student_id")
    public Map<Integer, Student> getAllStudents();

    @SqlUpdate("INSERT INTO students (first_name, middle_name, last_name, subjects, date_added) VALUES (?, ?, ?, ?, ?)")
    @GetGeneratedKeys // gets the new id of the student
    public Integer insertStudent(String firstName, String middleName, String lastName, Integer subjects,
            Instant dateAdded);

    @SqlUpdate("DELETE FROM students WHERE student_id = ?")
    public boolean deleteStudent(Integer studentId);

    @SqlQuery("SELECT v.*, s.first_name, s.middle_name, s.last_name FROM visits v INNER JOIN students s ON v.student_id = s.student_id WHERE v.visit_id = ?;")
    @RegisterRowMapper(RowToVisitMapper.class)
    public Visit getVisit(Integer visitId);

    @SqlQuery("SELECT v.*, s.first_name, s.middle_name, s.last_name FROM visits v INNER JOIN students s ON v.student_id = s.student_id;")
    @RegisterRowMapper(RowToVisitMapper.class)
    @KeyColumn("visit_id")
    public Map<Integer, Visit> getAllVisits();

    @SqlQuery("SELECT v.*, s.first_name, s.middle_name, s.last_name FROM visits v INNER JOIN students s ON v.student_id = s.student_id;")
    @RegisterRowMapper(RowToVisitMapper.class)
    @KeyColumn("visit_id")
    public Map<Integer, Visit> getOngoingVisits();

    @SqlUpdate("INSERT INTO visits (start_time, end_time, student_id) VALUES (?, ?, ?)")
    @GetGeneratedKeys // gets the new id of the visit
    public Integer insertVisit(Instant startTime, Instant endTime, Integer studentId);

    @SqlUpdate("DELETE FROM visits WHERE student_id = ?")
    public boolean deleteStudentVisits(Integer studentId);

    @SqlUpdate("UPDATE visits SET end_time = ? WHERE visit_id = ?")
    public void updateVisitEndtime(Instant endTime, Integer visitId);

}
