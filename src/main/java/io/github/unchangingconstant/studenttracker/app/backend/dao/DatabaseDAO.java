package io.github.unchangingconstant.studenttracker.app.backend.dao;

import java.time.Instant;
import java.util.Map;

import org.jdbi.v3.sqlobject.config.KeyColumn;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import io.github.unchangingconstant.studenttracker.app.backend.entities.OngoingVisit;
import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.backend.entities.Visit;
import io.github.unchangingconstant.studenttracker.app.backend.mappers.RowToOngoingVisitMapper;
import io.github.unchangingconstant.studenttracker.app.backend.mappers.RowToStudentMapper;
import io.github.unchangingconstant.studenttracker.app.backend.mappers.RowToVisitMapper;

// Read up on mappers, section 7 of JDBI docs
public interface DatabaseDAO {

    /*
     * STUDENT METHODS
     */
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

    /*
     * VISIT METHODS
     */
    @SqlQuery("SELECT * FROM visits WHERE visit_id = ?")
    @RegisterRowMapper(RowToVisitMapper.class)
    public Visit getVisit(Integer visitId);

    @SqlQuery("SELECT * FROM visits")
    @RegisterRowMapper(RowToVisitMapper.class)
    @KeyColumn("visit_id")
    public Map<Integer, Visit> getAllVisits();

    @SqlUpdate("INSERT INTO visits (start_time, end_time, student_id) VALUES (?, ?, ?)")
    @GetGeneratedKeys // gets the new id of the visit
    public Integer insertVisit(Instant startTime, Instant endTime, Integer studentId);

    @SqlUpdate("DELETE FROM visits WHERE visit_id = ?")
    public boolean deleteVisit(Integer visitId);

    @SqlUpdate("DELETE FROM visits WHERE student_id = ?")
    public boolean deleteStudentVisits(Integer studentId);

    /*
     * ONGOING VISIT METHODS
     */
    @SqlQuery("SELECT * FROM ongoing_visits WHERE studentId = ?")
    @RegisterRowMapper(RowToVisitMapper.class)
    public OngoingVisit getOngoingVisit(Integer studentId);

    @SqlQuery("SELECT ov.*, s.first_name, s.middle_name, s.last_name FROM ongoing_visits ov INNER JOIN students s ON ov.student_id = s.student_id;")
    @RegisterRowMapper(RowToOngoingVisitMapper.class)
    @KeyColumn("visit_id")
    public Map<Integer, OngoingVisit> getOngoingVisits();

    @SqlUpdate("INSERT INTO ongoing_visits (student_id, start_time) VALUES (?, ?)")
    public void insertOngoingVisit(Integer studentId, Instant startTime);

    @SqlUpdate("DELETE FROM ongoing_visits WHERE student_id = ?")
    public void deleteOngoingVisit(Integer studentId);

}
