package com.github.unchangingconstant.studenttracker.app.dbmanager;

import static org.junit.jupiter.api.Assertions.*;
import static org.instancio.Select.field;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.instancio.Instancio;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.testing.junit5.JdbiExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.unchangingconstant.studenttracker.app.entities.Student;
import com.github.unchangingconstant.studenttracker.app.entities.StudentTestUtil;
import com.github.unchangingconstant.studenttracker.app.entities.Visit;
import com.github.unchangingconstant.studenttracker.app.mappers.domain.RowToStudentMapper;
import com.github.unchangingconstant.studenttracker.app.mappers.domain.RowToVisitMapper;
import com.github.unchangingconstant.studenttracker.guice.DatabaseManagerModule;
import com.github.unchangingconstant.studenttracker.util.ResourceLoader;

/**
 * Turns out, JUnit5 has a lot of magic to it. To understand everything that's
 * happening here, you're gonna need to read up on that. i.e. RegisterExtension
 * annotation??? Huh????
 */
public class AttendanceDAOTest {

    @RegisterExtension // this annotation has something to do with the magic behind junit5 and stuff
    // Creates tool which creates in-mem sqlite database at test time
    private static final JdbiExtension sqliteExtension = JdbiExtension.sqlite().withPlugin(new SqlObjectPlugin());
    private AttendanceDAO dao;
    private Jdbi jdbi;

    // Can we centralize these somehow?
    // @UseClassPathResource is possible but hard to implement how we want it
    // Plus, having SQL written in the DAO works pretty well
    private final String STUDENT_TABLE = ResourceLoader.loadSQL("/schema/student_table");
    private final String VISIT_TABLE = ResourceLoader.loadSQL("/schema/visit_table");
    private final String ONGOING_VISIT_TABLE = ResourceLoader.loadSQL("/schema/ongoing_visit_table");
    // THESE QUERIES ARE FOR TESTS ONLY! THEY ARE NOT LIKE THE DAO QUERIES, NOT INTERCHANGEABLE
    // This is because these also insert the id, unlike the DAO methods, which assign ids upon insert
    private final String INSERT_STUDENT = "INSERT INTO students (student_id, full_name, preferred_name, visit_time, date_added) VALUES (:studentId, :fullName, :preferredName, :visitTime, :dateAdded);";
    private final String INSERT_VISIT = "INSERT INTO visits (visit_id, student_id, start_time, duration) VALUES (:visitId, :studentId, :startTime, :duration);";
    private final String SELECT_STUDENT = "SELECT * FROM students WHERE student_id = ?;";

    @BeforeEach
    void setUp() {
        jdbi = sqliteExtension.getJdbi();
        jdbi.registerRowMapper(new RowToStudentMapper()).registerRowMapper(new RowToVisitMapper());
        jdbi.useHandle(handle -> handle.execute(STUDENT_TABLE));
        jdbi.useHandle(handle -> handle.execute(VISIT_TABLE));
        jdbi.useHandle(handle -> handle.execute(ONGOING_VISIT_TABLE));
        dao = (new DatabaseManagerModule()).provideDatabaseDAO(jdbi); // Figure out how Guice fits into testing
    }

    @AfterEach
    void tearDown() {
        jdbi.useHandle(handle -> handle.execute("DROP TABLE students;"));
        jdbi.useHandle(handle -> handle.execute("DROP TABLE visits;"));
        jdbi.useHandle(handle -> handle.execute("DROP TABLE ongoing_visits;"));
    }

//    @Test
//    void smokeTest() {
//        Student student = StudentTestUtil.student().create();
//        int genId = jdbi.withHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute());
//        Student result = jdbi
//            .withHandle(handle -> handle.createQuery(SELECT_STUDENT).bind(0, genId).mapTo(Student.class).one());
//        System.out.println(result);
//    }

    /**
     * STUDENT TESTS START HERE
     */
    @Test
    @DisplayName("findStudent() maps query result to Student object")
    void testFindStudent_1() {
        Student expected = StudentTestUtil.student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(expected).execute());
        assertEquals(Optional.of(expected), dao.findStudent(expected.getStudentId()));
    }

    @Test
    @DisplayName("findStudent() returns empty Optional on non-existent ID")
    void testFindStudent_3() {
        Student expected = StudentTestUtil.student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(expected).execute());
        assertEquals(Optional.empty(), dao.findStudent(expected.getStudentId() + 1));
    }

    @Test
    @DisplayName("findStudent() gets the right student")
    void testFindStudent_4() {
        Student expected = StudentTestUtil.student().set(field(Student::getStudentId), 2).create();
        List<Student> sample = Instancio.createList(Student.class);
        sample.add(1, expected);
        sample.forEach(
                student -> jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute()));

        assertEquals(Optional.of(expected), dao.findStudent(2));
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
    @DisplayName("getAllStudents returns empty map if dao returns empty map")
    void testGetAllStudents_2() {
        assertEquals(Instancio.ofMap(Integer.class, Student.class).size(0).create(), dao.getAllStudents());
    }

    @Test
    @DisplayName("insertStudent() inserts students correctly")
    void testInsertStudent_1() {
        // This sucks. Just hoping the dao assigns it an ID of "1"
        Student expected = StudentTestUtil.student().set(field(Student::getStudentId), 1).create();
        Integer resultId = dao.insertStudent(expected);
        Student result = jdbi
            .withHandle(handle -> handle.createQuery(SELECT_STUDENT).bind(0, resultId).mapTo(Student.class).one());
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("deleteStudent() returns true on successful delete")
    void testDeleteStudent_1() {
        Student student = StudentTestUtil.student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute());
        assertTrue(dao.deleteStudent(student.getStudentId()));
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
    @DisplayName("deleteStudent() deletes student and their corresponding visits from the visits table")
    void testDeleteStudent_4() {
        Student student = StudentTestUtil.student().create();
        Visit visit = Instancio.of(Visit.class).set(field(Visit::getStudentId), student.getStudentId()).create();

        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_VISIT).bindBean(visit).execute());

        dao.deleteStudent(student.getStudentId());

        List<Student> dbStudents = jdbi.withHandle(
            handle -> handle.createQuery("SELECT * FROM students;").mapTo(Student.class).list());
        List<Visit> dbVisits = jdbi.withHandle(
            handle -> handle.createQuery("SELECT * FROM visits;").mapTo(Visit.class).list());

        assertTrue(dbStudents.isEmpty());
        assertTrue(dbVisits.isEmpty());
    }

    @Test
    @DisplayName("updateStudent() returns false if student with studentId doesn't exist in the database")
    void testUpdateStudent_1() {
        // TODO insert multiple students into database for this test
        Student s1 = StudentTestUtil.validStudent().create();
        Student s2 = StudentTestUtil.validStudent().set(field(Student::getStudentId), Math.abs(s1.getStudentId() + 1)).create();

        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1));

        assertFalse(dao.updateStudent(s2));
    }

    /**
     * VISIT TESTS START HERE
     */
    @Test
    @DisplayName("findVisit() gets the visit with the corresponding id")
    void testFindVisit_1() {
        Visit v = Instancio.create(Visit.class);
        Student s = StudentTestUtil.student().create();
    }

}
