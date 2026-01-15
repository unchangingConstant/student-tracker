package com.github.unchangingconstant.studenttracker.app.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.instancio.Select.field;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.instancio.Instancio;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.testing.junit5.JdbiExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;
import com.github.unchangingconstant.studenttracker.app.domain.Student;
import com.github.unchangingconstant.studenttracker.app.domain.StudentTestUtil;
import com.github.unchangingconstant.studenttracker.app.domain.Visit;
import com.github.unchangingconstant.studenttracker.app.mappers.domain.RowToStudentMapper;
import com.github.unchangingconstant.studenttracker.app.mappers.domain.RowToVisitMapper;
import com.github.unchangingconstant.studenttracker.guice.DAOModule;
import com.github.unchangingconstant.studenttracker.util.ResourceLoader;

/**
 * Turns out, JUnit5 has a lot of magic to it. To understand everything that's
 * happening here, you're gonna need to read up on that. i.e. RegisterExtension
 * annotation??? Huh????
 */
public class DatabaseDAOTest {

    @RegisterExtension // this annotation has something to do with the magic behind junit5 and stuff
    // Creates tool which creates in-mem sqlite database at test time
    private final JdbiExtension sqliteExtension = JdbiExtension.sqlite().withPlugin(new SqlObjectPlugin());
    private DatabaseDAO dao;
    private Jdbi jdbi;

    private final String STUDENT_TABLE = ResourceLoader.loadResource("/sql/schema/studentTable.sql");
    private final String VISIT_TABLE = ResourceLoader.loadResource("/sql/schema/visitTable.sql");
    private final String ONGOING_VISIT_TABLE = ResourceLoader.loadResource("/sql/schema/ongoingVisitTable.sql");
    private final String INSERT_STUDENT = "INSERT INTO students (student_id, full_legal_name, preferred_name, subjects, date_added) VALUES (:studentId, :fullLegalName, :prefName, :subjects, :dateAdded)";
    private final String INSERT_VISIT = "INSERT INTO visits (visit_id, student_id, start_time, end_time) VALUES (:visitId, :studentId, :startTime, :endTime)";
    private final String SELECT_STUDENT = "SELECT * FROM students WHERE student_id = ?";

    @BeforeEach
    void setUp() {
        jdbi = sqliteExtension.getJdbi();
        jdbi.registerRowMapper(new RowToStudentMapper()).registerRowMapper(new RowToVisitMapper());
        jdbi.withHandle(handle -> handle.execute(STUDENT_TABLE));
        jdbi.withHandle(handle -> handle.execute(VISIT_TABLE));
        jdbi.withHandle(handle -> handle.execute(ONGOING_VISIT_TABLE));
        dao = (new DAOModule()).provideDatabaseDAO(jdbi); // Figure out how Guice fits into testing
    }

    /**
     * STUDENT TESTS START HERE
     */
    @Test
    @DisplayName("getStudent() maps query result to Student object")
    void testGetStudent_1() {
        Student expected = StudentTestUtil.student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(expected).execute());
        dao.getStudent(expected.getStudentId());
        assertEquals(expected, dao.getStudent(expected.getStudentId()));
    }

    @Test
    @DisplayName("getStudent() returns null on non-existant ID")
    void testGetStudent_3() {
        Student expected = StudentTestUtil.student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(expected).execute());
        assertEquals(null, dao.getStudent(expected.getStudentId() + 1));
    }

    @Test
    @DisplayName("getStudent() gets the right student")
    void testGetStudent_4() {
        Student expected = StudentTestUtil.student().set(field(Student::getStudentId), 2).create();
        List<Student> sample = Instancio.createList(Student.class);
        sample.add(1, expected);
        sample.forEach(
                student -> jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute()));

        assertEquals(expected, dao.getStudent(2));
    }

    @Test
    @DisplayName("getAllStudents() maps query result to <studentId, student> map.")
    void testGetAllStudents_1() {
        Student s1 = StudentTestUtil.student().create();
        Student s2 = StudentTestUtil.student().create();
        Student s3 = StudentTestUtil.student().create();

        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s2).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s3).execute());

        Map<Integer, Student> map = dao.getAllStudents();

        assertEquals(s1, map.get(s1.getStudentId()));
        assertEquals(s2, map.get(s2.getStudentId()));
        assertEquals(s3, map.get(s3.getStudentId()));
    }

    @Test
    @DisplayName("getAllStudents returns empty map if query has no rows")
    void testGetAllStudents_2() {
        assertEquals(Instancio.ofMap(Integer.class, Student.class).size(0).create(), dao.getAllStudents());
    }

    // TODO for the next method, at some point refactor to gather results using
    // jdbi handle.
    @Test
    @DisplayName("insertStudent() inserts students correctly")
    void testInsertStudent_1() {
        // This sucks. Just hoping the dao assigns it an ID of "1"
        Student s = StudentTestUtil.student().set(field(Student::getStudentId), 1).create();
        Integer resultId = dao.insertStudent(s.getFullLegalName(), s.getPrefName(), s.getVisitTime(), s.getDateAdded());
        Student result = jdbi
                .withHandle(handle -> handle.createQuery(SELECT_STUDENT).bind(0, resultId).mapTo(Student.class).one());
        assertEquals(s, result);
    }

    @Test
    @DisplayName("deleteStudent() returns true on successful delete")
    void testDeleteStudent_1() {
        Student s = StudentTestUtil.student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s).execute());
        assertTrue(dao.deleteStudent(s.getStudentId()));
    }

    @Test
    @DisplayName("deleteStudent() returns false on failed delete")
    void testDeleteStudent_2() {
        Student s = StudentTestUtil.student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s).execute());
        assertFalse(dao.deleteStudent(s.getStudentId() + 1));
    }

    @Test
    @DisplayName("deleteStudent() deletes the correct entry")
    void testDeleteStudent_3() {
        List<Student> list = Instancio.createList(Student.class);
        Student removed = StudentTestUtil.student().create();
        list.add(1, removed); // Make removed element the 2nd element of the list
        list.forEach(
                student -> jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute()));

        dao.deleteStudent(removed.getStudentId());

        Optional<Student> result = jdbi
                .withHandle(handle -> handle.createQuery(SELECT_STUDENT).bind(0, 2).mapTo(Student.class).findOne());
        assertFalse(result.isPresent()); // student we removed should have ID of 2
    }

    @Test
    @DisplayName("deleteStudent() doesn't delete student when student has visits in the visits table")
    void testDeleteStudent_4() {
        Student s = StudentTestUtil.student().create();
        Visit v = Instancio.of(Visit.class).set(field(Visit::getStudentId), s.getStudentId()).create();

        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_VISIT).bindBean(v).execute());

        assertThrows(UnableToExecuteStatementException.class, () -> dao.deleteStudent(s.getStudentId()));
        Student result = jdbi
                .withHandle(handle -> handle.createQuery(SELECT_STUDENT).bind(0, s.getStudentId()).mapTo(Student.class)
                        .one());
        assertEquals(s, result);
    }

    @Test
    @DisplayName("updateStudent() returns 0 if student with studentId doesn't exist in the database")
    void testUpdateStudent_1() {
        // TODO insert multiple students into database for this test
        Student s1 = StudentTestUtil.validStudent().create();
        Student s2 = StudentTestUtil.validStudent().set(field(Student::getStudentId), Math.abs(s1.getStudentId() + 1)).create();

        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1));

        assertEquals(0,
                dao.updateStudent(s2.getFullLegalName(), s2.getPrefName(), s2.getVisitTime(), s2.getStudentId()));
    }

    /**
     * VISIT TESTS START HERE
     */
    @Test
    @DisplayName("getVisit() gets the visit with the corresponding id")
    void testGetVisit_1() {
        Visit v = Instancio.create(Visit.class);
        Student s = StudentTestUtil.student().create();
    }

}
