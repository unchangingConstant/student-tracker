package com.github.unchangingconstant.studenttracker.app.dao;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.jdbi.v3.sqlobject.config.KeyColumn;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.github.unchangingconstant.studenttracker.app.domain.OngoingVisit;
import com.github.unchangingconstant.studenttracker.app.domain.Student;
import com.github.unchangingconstant.studenttracker.app.domain.Visit;
import com.github.unchangingconstant.studenttracker.app.mappers.domain.RowToOngoingVisitMapper;
import com.github.unchangingconstant.studenttracker.app.mappers.domain.RowToStudentMapper;
import com.github.unchangingconstant.studenttracker.app.mappers.domain.RowToVisitMapper;

// Read up on mappers, section 7 of JDBI docs
// TODO consider returning Optionals
public interface DatabaseDAO {

    /*
     * STUDENT METHODS
     */
    @SqlQuery("SELECT * FROM students WHERE student_id = ?")
    @RegisterRowMapper(RowToStudentMapper.class)
    public Student getStudent(Integer studentId);

    @SqlQuery("SELECT * FROM students WHERE student_id IN (<studentIds>)")
    @RegisterRowMapper(RowToStudentMapper.class)
    public List<Student> getStudents(@BindList("studentIds") List<Integer> studentIds);

    @SqlQuery("SELECT * FROM students")
    @RegisterRowMapper(RowToStudentMapper.class)
    @KeyColumn("student_id")
    public Map<Integer, Student> getAllStudents();

    @SqlUpdate("INSERT INTO students (full_legal_name, preferred_name, subjects, date_added) VALUES (?, ?, ?, ?)")
    @GetGeneratedKeys // gets the new id of the student
    public Integer insertStudent(String fullLegalName, String prefName, Integer subjects, Instant dateAdded);

    @SqlUpdate("DELETE FROM students WHERE student_id = ?")
    public boolean deleteStudent(Integer studentId);

    @SqlUpdate("UPDATE students SET full_legal_name = ?, preferred_name = ?, subjects = ? WHERE student_id = ?")
    public Integer updateStudent(String fullLegalName, String prefName, Integer subjects, Integer studentId);

    // You didn't finish this one
    // @SqlUpdate("DELETE FROM students")
    // public boolean hardDeleteStudent();

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

    @SqlQuery("SELECT * FROM visits WHERE student_id = ?")
    @RegisterRowMapper(RowToVisitMapper.class)
    public List<Visit> getStudentVisits(Integer studentId);

    @SqlUpdate("INSERT INTO visits (start_time, end_time, student_id) VALUES (?, ?, ?)")
    @GetGeneratedKeys // gets the new id of the visit
    public Integer insertVisit(Instant startTime, Instant endTime, Integer studentId);

    @SqlUpdate("DELETE FROM visits WHERE visit_id = ?")
    public boolean deleteVisit(Integer visitId);

    @SqlUpdate("DELETE FROM visits WHERE student_id = ?")
    public boolean deleteStudentVisits(Integer studentId);

    @SqlQuery("SELECT * FROM visits WHERE student_id IN (<studentIds>)")
    @RegisterRowMapper(RowToVisitMapper.class)
    public List<Visit> getMultipleStudentsVisits(@BindList("studentIds") List<Integer> studentIds);

    /*
     * ONGOING VISIT METHODS
     */
    @SqlQuery("SELECT ov.*, s.full_legal_name, s.preferred_name, s.subjects FROM ongoing_visits ov INNER JOIN students s ON ov.student_id = s.student_id where s.student_id = ?;")
    @RegisterRowMapper(RowToOngoingVisitMapper.class)
    public OngoingVisit getOngoingVisit(Integer studentId);

    @SqlQuery("SELECT ov.*, s.full_legal_name, s.preferred_name, s.subjects FROM ongoing_visits ov INNER JOIN students s ON ov.student_id = s.student_id;")
    @RegisterRowMapper(RowToOngoingVisitMapper.class)
    @KeyColumn("student_id")
    public Map<Integer, OngoingVisit> getOngoingVisits();

    @SqlUpdate("INSERT INTO ongoing_visits (student_id, start_time) VALUES (?, ?)")
    public void insertOngoingVisit(Integer studentId, Instant startTime);

    @SqlUpdate("DELETE FROM ongoing_visits WHERE student_id = ?")
    public void deleteOngoingVisit(Integer studentId);

}
