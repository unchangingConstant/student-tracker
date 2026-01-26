package com.github.unchangingconstant.studenttracker.app.dbmanager;

import java.util.List;
import java.util.Optional;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.github.unchangingconstant.studenttracker.app.entities.OngoingVisit;
import com.github.unchangingconstant.studenttracker.app.entities.Student;
import com.github.unchangingconstant.studenttracker.app.entities.Visit;
import com.github.unchangingconstant.studenttracker.app.mappers.domain.RowToOngoingVisitMapper;
import com.github.unchangingconstant.studenttracker.app.mappers.domain.RowToStudentMapper;
import com.github.unchangingconstant.studenttracker.app.mappers.domain.RowToVisitMapper;

// Read up on mappers, section 7 of JDBI docs
public interface AttendanceDAO {

    /*
     * STUDENT METHODS
     */
    @SqlQuery("SELECT * FROM students WHERE student_id = ?")
    @RegisterRowMapper(RowToStudentMapper.class)
    Optional<Student> findStudent(Integer studentId);

    @SqlQuery("SELECT * FROM students WHERE student_id IN (<studentIds>)")
    @RegisterRowMapper(RowToStudentMapper.class)
    List<Student> findStudentsWithId(@BindList("studentIds") List<Integer> studentIds);

    @SqlQuery("SELECT * FROM students")
    @RegisterRowMapper(RowToStudentMapper.class)
    List<Student> getAllStudents();

    @SqlUpdate("INSERT INTO students (full_name, preferred_name, subjects, date_added) VALUES (:fullName, :preferredName, :visitTime, :dateAdded)")
    @GetGeneratedKeys // gets the new id of the student
    Integer insertStudent(@BindBean Student student);

    @SqlUpdate("DELETE FROM students WHERE student_id = ?")
    boolean deleteStudent(Integer studentId);

    @SqlUpdate("UPDATE students SET full_name = :fullName, preferred_name = :preferredName, visit_time = :visitTime WHERE student_id = :studentId")
    boolean updateStudent(@BindBean Student student);

    /*
     * VISIT METHODS
     */
    @SqlQuery("SELECT * FROM visits WHERE visit_id = ?")
    @RegisterRowMapper(RowToVisitMapper.class)
    Optional<Visit> findVisit(Integer visitId);

    @SqlQuery("SELECT * FROM visits")
    @RegisterRowMapper(RowToVisitMapper.class)
    List<Visit> getAllVisits();

    @SqlQuery("SELECT * FROM visits WHERE student_id = ?")
    @RegisterRowMapper(RowToVisitMapper.class)
    List<Visit> findVisitsWithStudentId(Integer studentId);

    @SqlUpdate("INSERT INTO visits (start_time, duration, student_id) VALUES (:startTime, :duration, :studentId)")
    @GetGeneratedKeys // gets the new id of the visit
    Integer insertVisit(@BindBean Visit visit);

    @SqlUpdate("DELETE FROM visits WHERE visit_id = ?")
    boolean deleteVisit(Integer visitId);

    @SqlQuery("SELECT * FROM visits WHERE student_id IN (<studentIds>)")
    @RegisterRowMapper(RowToVisitMapper.class)
    List<Visit> getMultipleStudentsVisits(@BindList("studentIds") List<Integer> studentIds);

    /*
     * ONGOING VISIT METHODS
     */
    @SqlQuery("SELECT * FROM ongoing_visits WHERE student_id = ?;")
    @RegisterRowMapper(RowToOngoingVisitMapper.class)
    Optional<OngoingVisit> findOngoingVisit(Integer studentId);

    @SqlQuery("SELECT ov.*, s.* FROM ongoing_visits ov INNER JOIN students s ON ov.student_id = s.student_id;")
    @RegisterRowMapper(RowToOngoingVisitMapper.class)
    List<OngoingVisit> getOngoingVisits();

    @SqlUpdate("INSERT INTO ongoing_visits (student_id, start_time) VALUES (:studentId, :startTime)")
    void insertOngoingVisit(@BindBean OngoingVisit ongoingVisit);

    @SqlUpdate("DELETE FROM ongoing_visits WHERE student_id = :studentId; INSERT INTO visits (start_time, duration, student_id) VALUES (:startTime, :duration, :studentId)")
    void endOngoingVisit(@BindBean Visit visit);

}
