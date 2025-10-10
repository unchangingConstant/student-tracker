package io.github.unchangingconstant.studenttracker.app.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.Select.*;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.testing.junit5.JdbiExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;
import io.github.unchangingconstant.studenttracker.app.entities.Student;
import io.github.unchangingconstant.studenttracker.utils.ScriptLoader;

/**
 * Turns out, JUnit5 has a lot of magic to it. To understand everything that's
 * happening here, you're gonna need to read up on that. i.e. RegisterExtension
 * annotation??? Huh????
 */
public class DatabaseDAOTest {

    @RegisterExtension // this annotation has something to do with the magic behind junit5 and stuff
    // Creates tool which creates in-mem sqlite database at test time
    private final JdbiExtension sqliteExtension = JdbiExtension.sqlite().withPlugin(new SqlObjectPlugin());
    private final String createSchemaScript = ScriptLoader.loadSqlScript("/sql/schema.sql");
    private DatabaseDAO dao;
    private Jdbi jdbi;

    private final String INSERT_STUDENT = "INSERT INTO students (student_id, first_name, last_name, middle_name, subjects) VALUES (:studentId, :firstName, :lastName, :middleName, :subjects)";
    private final String SELECT_STUDENT = "SELECT * FROM students WHERE student_id = ?";

    @BeforeEach
    void setUp() {
        jdbi = sqliteExtension.getJdbi();
        jdbi.useHandle(handle -> handle.execute(createSchemaScript));
        dao = jdbi.onDemand(DatabaseDAO.class);
    }

    @Test
    @DisplayName("getStudent() maps query result to Student object")
    void testGetStudent_1() {
        Student expected = Instancio.create(Student.class);
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(expected).execute());
        assertEquals(expected, dao.getStudent(expected.getStudentId()));
    }

    @Test
    @DisplayName("getStudent() maps NULL middle_name as null middleName in Student object")
    void testGetStudent_2() {
        Student expected = Instancio.create(Student.class);
        expected.setMiddleName(null);
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(expected).execute());
        assertEquals(expected, dao.getStudent(expected.getStudentId()));
    }

    @Test
    @DisplayName("getStudent() returns null on non-existant ID")
    void testGetStudent_3() {
        Student expected = Instancio.create(Student.class);
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(expected).execute());
        assertEquals(null, dao.getStudent(expected.getStudentId() - 1));
    }

    @Test
    @DisplayName("getAllStudents() maps query result to <studentId, student> map.")
    void testGetAllStudents_1() {
        Student s1 = Instancio.create(Student.class);
        Student s2 = Instancio.create(Student.class);
        Student s3 = Instancio.create(Student.class);

        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s2).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s3).execute());

        Map<Integer, Student> map = dao.getAllStudents();

        assertEquals(s1, map.get(s1.getStudentId()));
        assertEquals(s2, map.get(s2.getStudentId()));
        assertEquals(s3, map.get(s3.getStudentId()));
    }

    // TODO for the next two methods, at some point refactor to gather results using
    // jdbi handle.
    @Test
    @DisplayName("insertStudent() inserts students correctly")
    void testInsertStudent_1() {
        Student s = Instancio.create(Student.class);
        s.setStudentId(dao.insertStudent(s.getFirstName(), s.getMiddleName(), s.getLastName(), s.getSubjects()));
        assertEquals(s, dao.getStudent(s.getStudentId()));
    }

    @Test
    @DisplayName("insertStudent() inserts null middleName correctly")
    void testInsertStudent_2() {
        Student s = Instancio.create(Student.class);
        s.setMiddleName(null);
        s.setStudentId(dao.insertStudent(s.getFirstName(), s.getMiddleName(), s.getLastName(), s.getSubjects()));
        assertEquals(s, dao.getStudent(s.getStudentId()));
    }

    @Test
    @DisplayName("deleteStudent() returns true on successful delete")
    void testDeleteStudent_1() {
        Student s = Instancio.create(Student.class);
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s).execute());
        assertTrue(dao.deleteStudent(s.getStudentId()));
    }

    @Test
    @DisplayName("deleteStudent() returns false on failed delete")
    void testDeleteStudent_2() {
        Student s = Instancio.create(Student.class);
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s).execute());
        assertFalse(dao.deleteStudent(s.getStudentId() + 1));
    }

}
