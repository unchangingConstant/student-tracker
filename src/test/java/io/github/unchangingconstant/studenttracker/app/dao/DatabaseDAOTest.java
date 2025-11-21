package io.github.unchangingconstant.studenttracker.app.dao;

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

import io.github.unchangingconstant.studenttracker.app.domain.StudentDomain;
import io.github.unchangingconstant.studenttracker.app.domain.StudentTestUtil;
import io.github.unchangingconstant.studenttracker.app.domain.VisitDomain;
import io.github.unchangingconstant.studenttracker.config.DatabaseModule;
import io.github.unchangingconstant.studenttracker.utils.ResourceLoader;
import io.github.unchangingconstant.studenttracker.app.mappers.domain.RowToStudentMapper;
import io.github.unchangingconstant.studenttracker.app.mappers.domain.RowToVisitMapper;

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
        dao = DatabaseModule.provideDatabaseDAO(jdbi);
    }

    /**
     * STUDENT TESTS START HERE
     */
    @Test
    @DisplayName("getStudent() maps query result to Student object")
    void testGetStudent_1() {
        StudentDomain expected = StudentTestUtil.student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(expected).execute());
        dao.getStudent(expected.getStudentId());
        assertEquals(expected, dao.getStudent(expected.getStudentId()));
    }

    @Test
    @DisplayName("getStudent() returns null on non-existant ID")
    void testGetStudent_3() {
        StudentDomain expected = StudentTestUtil.student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(expected).execute());
        assertEquals(null, dao.getStudent(expected.getStudentId() + 1));
    }

    @Test
    @DisplayName("getStudent() gets the right student")
    void testGetStudent_4() {
        StudentDomain expected = StudentTestUtil.student().set(field(StudentDomain::getStudentId), 2).create();
        List<StudentDomain> sample = Instancio.createList(StudentDomain.class);
        sample.add(1, expected);
        sample.forEach(
                student -> jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute()));

        assertEquals(expected, dao.getStudent(2));
    }

    @Test
    @DisplayName("getAllStudents() maps query result to <studentId, student> map.")
    void testGetAllStudents_1() {
        StudentDomain s1 = StudentTestUtil.student().create();
        StudentDomain s2 = StudentTestUtil.student().create();
        StudentDomain s3 = StudentTestUtil.student().create();

        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s2).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s3).execute());

        Map<Integer, StudentDomain> map = dao.getAllStudents();

        assertEquals(s1, map.get(s1.getStudentId()));
        assertEquals(s2, map.get(s2.getStudentId()));
        assertEquals(s3, map.get(s3.getStudentId()));
    }

    @Test
    @DisplayName("getAllStudents returns empty map if query has no rows")
    void testGetAllStudents_2() {
        assertEquals(Instancio.ofMap(Integer.class, StudentDomain.class).size(0).create(), dao.getAllStudents());
    }

    // TODO for the next method, at some point refactor to gather results using
    // jdbi handle.
    @Test
    @DisplayName("insertStudent() inserts students correctly")
    void testInsertStudent_1() {
        // This sucks. Just hoping the dao assigns it an ID of "1"
        StudentDomain s = StudentTestUtil.student().set(field(StudentDomain::getStudentId), 1).create();
        Integer resultId = dao.insertStudent(s.getFullLegalName(), s.getPrefName(), s.getSubjects(), s.getDateAdded());
        StudentDomain result = jdbi
                .withHandle(handle -> handle.createQuery(SELECT_STUDENT).bind(0, resultId).mapTo(StudentDomain.class).one());
        assertEquals(s, result);
    }

    @Test
    @DisplayName("deleteStudent() returns true on successful delete")
    void testDeleteStudent_1() {
        StudentDomain s = StudentTestUtil.student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s).execute());
        assertTrue(dao.deleteStudent(s.getStudentId()));
    }

    @Test
    @DisplayName("deleteStudent() returns false on failed delete")
    void testDeleteStudent_2() {
        StudentDomain s = StudentTestUtil.student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s).execute());
        assertFalse(dao.deleteStudent(s.getStudentId() + 1));
    }

    @Test
    @DisplayName("deleteStudent() deletes the correct entry")
    void testDeleteStudent_3() {
        List<StudentDomain> list = Instancio.createList(StudentDomain.class);
        StudentDomain removed = StudentTestUtil.student().create();
        list.add(1, removed); // Make removed element the 2nd element of the list
        list.forEach(
                student -> jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute()));

        dao.deleteStudent(removed.getStudentId());

        Optional<StudentDomain> result = jdbi
                .withHandle(handle -> handle.createQuery(SELECT_STUDENT).bind(0, 2).mapTo(StudentDomain.class).findOne());
        assertFalse(result.isPresent()); // student we removed should have ID of 2
    }

    @Test
    @DisplayName("deleteStudent() doesn't delete student when student has visits in the visits table")
    void testDeleteStudent_4() {
        StudentDomain s = StudentTestUtil.student().create();
        VisitDomain v = Instancio.of(VisitDomain.class).set(field(VisitDomain::getStudentId), s.getStudentId()).create();

        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_VISIT).bindBean(v).execute());

        assertThrows(UnableToExecuteStatementException.class, () -> dao.deleteStudent(s.getStudentId()));
        StudentDomain result = jdbi
                .withHandle(handle -> handle.createQuery(SELECT_STUDENT).bind(0, s.getStudentId()).mapTo(StudentDomain.class)
                        .one());
        assertEquals(s, result);
    }

    /**
     * VISIT TESTS START HERE
     */
    @Test
    @DisplayName("getVisit() gets the visit with the corresponding id")
    void testGetVisit_1() {
        VisitDomain v = Instancio.create(VisitDomain.class);
        StudentDomain s = StudentTestUtil.student().create();
    }

}
