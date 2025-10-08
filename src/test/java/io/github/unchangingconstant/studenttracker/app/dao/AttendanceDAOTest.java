package io.github.unchangingconstant.studenttracker.app.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.testing.junit5.JdbiExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;
import io.github.unchangingconstant.studenttracker.entities.Student;

/**
 * Turns out, JUnit5 has a lot of magic to it. To understand everything that's
 * happening here, you're gonna need to read up on that. i.e. RegisterExtension
 * annotation??? Huh????
 */
public class AttendanceDAOTest {

    @RegisterExtension // this annotation has something to do with the magic behind junit5 and stuff
    // Creates tool which creates in-mem sqlite database at test time
    private JdbiExtension sqliteExtension = JdbiExtension.sqlite().withPlugin(new SqlObjectPlugin());
    private String initDatabaseScript;
    private DatabaseDAO dao;

    @BeforeEach
    void setUp() {
        Jdbi jdbi = sqliteExtension.getJdbi();
        // try not to hardcode shit like this if you can help it
        // maybe a static method in the dao code to run this script?
        jdbi.useHandle(handle -> {
            handle.execute("""
                    CREATE TABLE IF NOT EXISTS students (
                        student_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        first_name TEXT NOT NULL,
                        middle_name TEXT,
                        last_name TEXT NOT NULL,
                        subjects INTEGER
                    )
                    """);
        });
        dao = jdbi.onDemand(DatabaseDAO.class);
    }

    @Test
    @DisplayName("insertStudent() handles student with no id")
    void testInsertNewStudent() {
        Student expected = new Student("Ethan", "Begley", "Labubu", (short) 2, 1);
        assertEquals(1, dao.insertStudent("Ethan", "Labubu", "Begley", (short) 2));
        Student result = dao.getStudent(1);
        assertEquals(1, result.getStudentId());
        assertEquals(expected, result);
    }

    // @Test
    // @DisplayName("deleteStudent() smoke test")
    // void testDeleteStudent() {
    // Student test = new Student("Ethan", "Begley", "Labubu", (short) 2, 1);
    // assertEquals(1, dao.insertStudent(test));
    // assertTrue(dao.deleteStudent(1));
    // assertTrue(dao.getAllStudents().isEmpty());
    // }

}
