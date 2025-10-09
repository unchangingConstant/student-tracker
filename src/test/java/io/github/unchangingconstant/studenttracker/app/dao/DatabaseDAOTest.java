package io.github.unchangingconstant.studenttracker.app.dao;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
    void setUp() {
        Jdbi jdbi = sqliteExtension.getJdbi();
        jdbi.useHandle(handle -> handle.execute(createSchemaScript));
        dao = jdbi.onDemand(DatabaseDAO.class);
    }

    @Test
    @DisplayName("getStudent(Integer) correctly returns student with given ID")
    void testGetStudent_1() {
        Student s = Instancio.create(Student.class);
    }

    @Test
    @DisplayName("insertStudent() handles student with no id")
    void testInsertNewStudent() {
        Student expected = Student.builder().studentId(1).firstName("John").middleName("Henry").lastName("Smith")
                .subjects((short) 2)
                .build();
        assertEquals(1, dao.insertStudent("John", "Henry", "Smith", (short) 2));
        Student result = dao.getStudent(1);
        assertEquals(1, result.getStudentId());
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("deleteStudent() smoke test")
    void testDeleteStudent() {
        assertEquals(1, dao.insertStudent("John", "Henry", "Smith", (short) 2));
        assertTrue(dao.deleteStudent(1));
        assertTrue(dao.getAllStudents().isEmpty());
    }

}
