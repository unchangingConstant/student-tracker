package io.github.unchangingconstant.studenttracker.app.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.instancio.Instancio;
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

    private final String INSERT = "INSERT INTO students (student_id, first_name, last_name, middle_name, subjects) VALUES (:studentId, :firstName, :lastName, :middleName, :subjects)";

    @BeforeEach
    void setUp() {
        jdbi = sqliteExtension.getJdbi();
        jdbi.useHandle(handle -> handle.execute(createSchemaScript));
        dao = jdbi.onDemand(DatabaseDAO.class);
    }

    @Test
    @DisplayName("getStudent(Integer) correctly returns student with given ID")
    void testGetStudent_1() {
        Student expected = Instancio.create(Student.class);
        jdbi.useHandle(handle -> handle.createUpdate(INSERT).bindBean(expected).execute());
        assertEquals(expected, dao.getStudent(expected.getStudentId()));
    }

    @Test
    @DisplayName("getAllStudents() returns all student in a map, multiple students in table case")
    void testGetAllStudents_1() {
        Student s1 = Instancio.create(Student.class);
        Student s2 = Instancio.create(Student.class);
        Student s3 = Instancio.create(Student.class);

        jdbi.useHandle(handle -> handle.createUpdate(INSERT).bindBean(s1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT).bindBean(s2).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT).bindBean(s3).execute());

        Map<Integer, Student> map = dao.getAllStudents();

        assertEquals(s1, map.get(s1.getStudentId()));
        assertEquals(s2, map.get(s2.getStudentId()));
        assertEquals(s3, map.get(s3.getStudentId()));
    }

}
