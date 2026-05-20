package com.github.unchangingconstant.studenttracker.app.dbmanager;

import static com.github.unchangingconstant.studenttracker.app.entities.EntityTestUtil.*;
import static org.instancio.Instancio.gen;
import static org.junit.jupiter.api.Assertions.*;
import static org.instancio.Select.field;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.unchangingconstant.studenttracker.app.entities.EntityTestUtil;
import com.github.unchangingconstant.studenttracker.app.entities.OngoingVisit;
import com.github.unchangingconstant.studenttracker.app.dbmanager.mappers.RowToOngoingVisitMapper;
import org.instancio.Instancio;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.testing.junit5.JdbiExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.unchangingconstant.studenttracker.app.entities.Student;
import com.github.unchangingconstant.studenttracker.app.entities.Visit;
import com.github.unchangingconstant.studenttracker.app.dbmanager.mappers.RowToStudentMapper;
import com.github.unchangingconstant.studenttracker.app.dbmanager.mappers.RowToVisitMapper;
import com.github.unchangingconstant.studenttracker.guice.DatabaseManagerModule;
import com.github.unchangingconstant.studenttracker.util.ResourceLoader;

import javax.swing.text.html.parser.Entity;

/*
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
    private final String INSERT_ONGOING_VISIT = "INSERT INTO ongoing_visits (student_id, start_time) VALUES (:studentId, :startTime);";
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

    /**
     * STUDENT TESTS START HERE
     */
    @Test
    @DisplayName("findStudent() maps query result to Student object")
    void testFindStudent_1() {
        Student expected = student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(expected).execute());
        assertEquals(Optional.of(expected), dao.findStudent(expected.getStudentId()));
    }

    @Test
    @DisplayName("findStudent() returns empty Optional on negative student ID")
    void testFindStudent_2() {
        assertEquals(Optional.empty(), dao.findStudent(-1));
    }

    @Test
    @DisplayName("findStudent() returns empty Optional on non-existent ID")
    void testFindStudent_3() {
        Student expected = student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(expected).execute());
        assertEquals(Optional.empty(), dao.findStudent(expected.getStudentId() + 1));
    }

    @Test
    @DisplayName("findStudent() gets the right student")
    void testFindStudent_4() {
        Student expected = student().set(field(Student::getStudentId), 2).create();
        List<Student> sample = Instancio.createList(Student.class);
        sample.add(1, expected);
        sample.forEach(
                student -> jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute()));

        assertEquals(Optional.of(expected), dao.findStudent(2));
    }

    /*
     * FIND STUDENTS WITH ID TESTS
     */
    @Test
    @DisplayName("findStudentsWithId() maps query result to <studentId, Student> map")
    void testFindStudentsWithId_1() {
        Student s1 = student().create();
        Student s2 = student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s2).execute());

        Map<Integer, Student> result = dao.findStudentsWithId(List.of(s1.getStudentId(), s2.getStudentId()));

        assertEquals(s1, result.get(s1.getStudentId()));
        assertEquals(s2, result.get(s2.getStudentId()));
    }

    @Test
    @DisplayName("findStudentsWithId() returns only students whose IDs are in the list")
    void testFindStudentsWithId_2() {
        Student s1 = student().create();
        Student s2 = student().create();
        Student s3 = student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s2).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s3).execute());

        Map<Integer, Student> result = dao.findStudentsWithId(List.of(s1.getStudentId(), s2.getStudentId()));

        assertEquals(2, result.size());
        assertEquals(s1, result.get(s1.getStudentId()));
        assertEquals(s2, result.get(s2.getStudentId()));
        assertNull(result.get(s3.getStudentId()));
    }

    @Test
    @DisplayName("findStudentsWithId() returns empty map when no IDs match")
    void testFindStudentsWithId_3() {
        Student s = student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s).execute());

        Map<Integer, Student> result = dao.findStudentsWithId(List.of(s.getStudentId() + 1));

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findStudentsWithId() throws error on empty studentIds list")
    void testFindStudentsWithId_4() {
        assertThrows(IllegalArgumentException.class, () -> dao.findStudentsWithId(List.of()).isEmpty());
    }

    @Test
    @DisplayName("getAllStudents() maps query result to <studentId, student> map.")
    void testGetAllStudents_1() {
        Student s1 = student().create();
        Student s2 = student().create();
        Student s3 = student().create();

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
        Student expected = student().set(field(Student::getStudentId), 1).create();
        Integer resultId = dao.insertStudent(expected);
        Student result = jdbi.withHandle(
                handle -> handle.createQuery(SELECT_STUDENT).bind(0, resultId).mapTo(Student.class).one());
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("deleteStudent() returns true on successful delete")
    void testDeleteStudent_1() {
        Student student = student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute());
        assertTrue(dao.deleteStudent(student.getStudentId()));
    }

    @Test
    @DisplayName("deleteStudent() returns false on failed delete")
    void testDeleteStudent_2() {
        Student s = student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s).execute());
        assertFalse(dao.deleteStudent(s.getStudentId() + 1));
    }

    @Test
    @DisplayName("deleteStudent() deletes the correct entry")
    void testDeleteStudent_3() {
        List<Student> list = Instancio.createList(Student.class);
        Student removed = student().create();
        list.add(removed);
        list.forEach(
                student -> jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute()));

        dao.deleteStudent(removed.getStudentId());

        Optional<Student> result = jdbi
                .withHandle(handle -> handle.createQuery(SELECT_STUDENT).bind(0, removed.getStudentId()).mapTo(Student.class).findOne());
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("deleteStudent() deletes student and their corresponding visits from the visits table")
    void testDeleteStudent_4() {
        Student student = student().create();
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
        Student s1 = EntityTestUtil.validStudent().create();
        Student s2 = EntityTestUtil.validStudent().set(field(Student::getStudentId), s1.getStudentId() + 1).create();

        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1).execute());

        assertFalse(dao.updateStudent(s2));
    }

    @Test
    @DisplayName("updateStudent() returns true when student is successfully updated")
    void testUpdateStudent_2() {
        Integer id = gen().ints().min(1).get();
        Student s1 = EntityTestUtil.validStudent().set(field(Student::getStudentId), id).create();
        // updateStudent does not update dateAdded, so make it the same
        Student s2 = EntityTestUtil.validStudent()
                .set(field(Student::getStudentId), id)
                .set(field(Student::getDateAdded), s1.getDateAdded())
                .create();

        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1).execute());

        assertTrue(dao.updateStudent(s2));
        // Check that student updated correctly
        Student updated = jdbi.withHandle(handle ->
                handle.createQuery(SELECT_STUDENT).bind(0, id).mapTo(Student.class).one());

        assertEquals(updated, s2);
    }

    /*
     * VISIT TESTS START HERE
     */

    @Test
    @DisplayName("SmokeTest")
    void testInsertVisit_1() {
        Student student = student().create();
        // Database should assign a visitId of one
        int visitId = 1;
        Visit visit = visit().set(field(Visit::getVisitId), visitId).set(field(Visit::getStudentId), student.getStudentId()).create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute());

        dao.insertVisit(visit);

        Visit result = jdbi.withHandle(
            handle -> handle.createQuery("SELECT * FROM visits WHERE visit_id = ?")
            .bind(0, visitId).map(new RowToVisitMapper()).one());

        assertEquals(result, visit);
    }

    /*
     * FIND VISITS WITH STUDENT ID TESTS
     */
    @Test
    @DisplayName("findVisitsWithStudentId() maps query result to list of Visit objects")
    void testFindVisitsWithStudentId_1() {
        Student student = student().create();
        Visit visit = visit().set(field(Visit::getStudentId), student.getStudentId()).create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_VISIT).bindBean(visit).execute());

        List<Visit> result = dao.findVisitsWithStudentId(student.getStudentId());

        assertEquals(List.of(visit), result);
    }

    @Test
    @DisplayName("findVisitsWithStudentId() returns empty list for student with no visits")
    void testFindVisitsWithStudentId_2() {
        Student student = student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute());

        List<Visit> result = dao.findVisitsWithStudentId(student.getStudentId());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findVisitsWithStudentId() returns only visits belonging to the given student")
    void testFindVisitsWithStudentId_3() {
        Student s1 = student().create();
        Student s2 = student().create();
        Visit v1 = visit().set(field(Visit::getStudentId), s1.getStudentId()).create();
        Visit v2 = visit().set(field(Visit::getStudentId), s2.getStudentId()).create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s2).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_VISIT).bindBean(v1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_VISIT).bindBean(v2).execute());

        List<Visit> result = dao.findVisitsWithStudentId(s1.getStudentId());

        assertEquals(List.of(v1), result);
    }

    /*
     * DELETE VISIT TESTS
     */
    @Test
    @DisplayName("deleteVisit() returns true on successful delete")
    void testDeleteVisit_1() {
        Student student = student().create();
        Visit visit = visit().set(field(Visit::getStudentId), student.getStudentId()).create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_VISIT).bindBean(visit).execute());

        assertTrue(dao.deleteVisit(visit.getVisitId()));
    }

    @Test
    @DisplayName("deleteVisit() returns false on failed delete")
    void testDeleteVisit_2() {
        int nonExistentId = gen().ints().min(1).get();

        assertFalse(dao.deleteVisit(nonExistentId));
    }

    @Test
    @DisplayName("deleteVisit() deletes the correct visit")
    void testDeleteVisit_3() {
        Student student = student().create();
        Visit v1 = visit().set(field(Visit::getStudentId), student.getStudentId()).create();
        Visit v2 = visit().set(field(Visit::getStudentId), student.getStudentId()).create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_VISIT).bindBean(v1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_VISIT).bindBean(v2).execute());

        dao.deleteVisit(v1.getVisitId());

        List<Visit> remaining = jdbi.withHandle(handle ->
            handle.createQuery("SELECT * FROM visits").map(new RowToVisitMapper()).list());
        assertFalse(remaining.contains(v1));
        assertTrue(remaining.contains(v2));
    }

    /*
     * GET MULTIPLE STUDENTS VISITS TESTS
     */
    @Test
    @DisplayName("getMultipleStudentsVisits() maps query result to list of Visit objects")
    void testGetMultipleStudentsVisits_1() {
        Student s1 = student().create();
        Visit v1 = visit().set(field(Visit::getStudentId), s1.getStudentId()).create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_VISIT).bindBean(v1).execute());

        List<Visit> result = dao.getMultipleStudentsVisits(List.of(s1.getStudentId()));

        assertEquals(List.of(v1), result);
    }

    // TODO use models to shorten code?
    // Look at Baeldung's Instancio ref
    @Test
    @DisplayName("getMultipleStudentsVisits() returns all visits for all specified students")
    void testGetMultipleStudentsVisits_2() {
        Student s1 = student().create();
        Student s2 = student().create();
        Visit v1a = visit().set(field(Visit::getStudentId), s1.getStudentId()).create();
        Visit v1b = visit().set(field(Visit::getStudentId), s1.getStudentId()).create();
        Visit v2a = visit().set(field(Visit::getStudentId), s2.getStudentId()).create();
        Visit v2b = visit().set(field(Visit::getStudentId), s2.getStudentId()).create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s2).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_VISIT).bindBean(v1a).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_VISIT).bindBean(v1b).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_VISIT).bindBean(v2a).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_VISIT).bindBean(v2b).execute());

        List<Visit> result = dao.getMultipleStudentsVisits(List.of(s1.getStudentId(), s2.getStudentId()));

        assertEquals(4, result.size());
        assertTrue(result.containsAll(List.of(v1a, v1b, v2a, v2b)));
    }

    @Test
    @DisplayName("getMultipleStudentsVisits() returns empty list when no visits match the given student IDs")
    void testGetMultipleStudentsVisits_3() {
        Student s1 = student().create();
        Student s2 = student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s2).execute());

        List<Visit> result = dao.getMultipleStudentsVisits(List.of(s1.getStudentId(), s2.getStudentId()));

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getMultipleStudentsVisits() behavior with empty studentIds list")
    void testGetMultipleStudentsVisits_4() {
        assertThrows(IllegalArgumentException.class, () -> dao.getMultipleStudentsVisits(List.of()));
    }

    /*
     * ONGOING_VISIT TESTS START HERE
     * TODO delete smoke tests, write some real tests
     */

    /*
     * FIND ONGOING VISIT TESTS
     */
    @Test
    @DisplayName("findOngoingVisit() maps query result to OngoingVisit object")
    void testFindOngoingVisit_1() {
        Student student = student().create();
        OngoingVisit ov = ongoingVisit().set(field(OngoingVisit::getStudentId), student.getStudentId()).create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_ONGOING_VISIT).bindBean(ov).execute());

        assertEquals(Optional.of(ov), dao.findOngoingVisit(student.getStudentId()));
    }

    @Test
    @DisplayName("findOngoingVisit() returns empty Optional when no ongoing visit exists for studentId")
    void testFindOngoingVisit_2() {
        Student student = student().create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute());

        assertEquals(Optional.empty(), dao.findOngoingVisit(student.getStudentId()));
    }

    @Test
    @DisplayName("findOngoingVisit() returns the right student's ongoing visit")
    void testFindOngoingVisit_3() {
        Student s1 = student().create();
        Student s2 = student().create();
        OngoingVisit ov1 = ongoingVisit().set(field(OngoingVisit::getStudentId), s1.getStudentId()).create();
        OngoingVisit ov2 = ongoingVisit().set(field(OngoingVisit::getStudentId), s2.getStudentId()).create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s2).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_ONGOING_VISIT).bindBean(ov1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_ONGOING_VISIT).bindBean(ov2).execute());

        assertEquals(Optional.of(ov1), dao.findOngoingVisit(s1.getStudentId()));
    }

    /*
     * GET ONGOING VISITS TESTS
     */
    @Test
    @DisplayName("getOngoingVisits() maps query result to <studentId, OngoingVisit> map")
    void testGetOngoingVisits_1() {
        Student s1 = student().create();
        Student s2 = student().create();
        OngoingVisit ov1 = ongoingVisit().set(field(OngoingVisit::getStudentId), s1.getStudentId()).create();
        OngoingVisit ov2 = ongoingVisit().set(field(OngoingVisit::getStudentId), s2.getStudentId()).create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s2).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_ONGOING_VISIT).bindBean(ov1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_ONGOING_VISIT).bindBean(ov2).execute());

        Map<Integer, OngoingVisit> result = dao.getOngoingVisits();

        assertEquals(ov1, result.get(s1.getStudentId()));
        assertEquals(ov2, result.get(s2.getStudentId()));
    }

    @Test
    @DisplayName("getOngoingVisits() returns empty map when no ongoing visits exist")
    void testGetOngoingVisits_2() {
        Map<Integer, OngoingVisit> result = dao.getOngoingVisits();

        assertTrue(result.isEmpty());
    }

    /*
     * INSERT ONGOING VISIT TESTS
     */
    @Test
    @DisplayName("insertOngoingVisit() inserts ongoing visit correctly")
    void testInsertOngoingVisit_1() {
        Student student = student().create();
        OngoingVisit ov = ongoingVisit().set(field(OngoingVisit::getStudentId), student.getStudentId()).create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute());

        dao.insertOngoingVisit(ov);

        Optional<OngoingVisit> result = jdbi.withHandle(handle ->
            handle.createQuery("SELECT * FROM ongoing_visits WHERE student_id = ?")
            .bind(0, student.getStudentId())
            .map(new RowToOngoingVisitMapper())
            .findOne());
        assertEquals(Optional.of(ov), result);
    }

    /*
     * DELETE ONGOING VISIT TESTS
     */
    @Test
    @DisplayName("deleteOngoingVisit() deletes the correct entry")
    void testDeleteOngoingVisit_1() {
        Student s1 = student().create();
        Student s2 = student().create();
        OngoingVisit ov1 = ongoingVisit().set(field(OngoingVisit::getStudentId), s1.getStudentId()).create();
        OngoingVisit ov2 = ongoingVisit().set(field(OngoingVisit::getStudentId), s2.getStudentId()).create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s2).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_ONGOING_VISIT).bindBean(ov1).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_ONGOING_VISIT).bindBean(ov2).execute());

        dao.deleteOngoingVisit(s1.getStudentId());

        List<OngoingVisit> remaining = jdbi.withHandle(handle ->
            handle.createQuery("SELECT * FROM ongoing_visits").map(new RowToOngoingVisitMapper()).list());
        assertFalse(remaining.contains(ov1));
        assertTrue(remaining.contains(ov2));
    }

    @Test
    @DisplayName("deleteOngoingVisit() does not throw when student has no ongoing visit")
    void testDeleteOngoingVisit_2() {
        int nonExistentId = gen().ints().min(1).get();

        assertDoesNotThrow(() -> dao.deleteOngoingVisit(nonExistentId));
    }

    /*
     * END ONGOING VISIT TESTS
     */
    @Test
    @DisplayName("Smoke test")
    void testEndOngoingVisit_1() {
        // TODO Create ongoing visit utils
        Student student = student().create();
        OngoingVisit ov = OngoingVisit.builder().studentId(student.getStudentId()).startTime(Instancio.create(Instant.class)).build();
        jdbi.useHandle(handle ->
            handle.createUpdate(INSERT_STUDENT).bindBean(student).execute());
        jdbi.useHandle(handle ->
            handle.createUpdate("INSERT INTO ongoing_visits (student_id, start_time) VALUES (:studentId, :startTime)")
            .bindBean(ov).execute());

        Visit visit = Visit.builder().studentId(student.getStudentId()).startTime(ov.getStartTime()).duration(gen().ints().min(1).get()).build();
        System.out.println(visit);

        dao.endOngoingVisit(visit);

        List<OngoingVisit> table = jdbi.withHandle(handle ->
            handle.createQuery("SELECT * FROM ongoing_visits").map(new RowToOngoingVisitMapper()).list());

        assertTrue(table.isEmpty());
    }

    @Test
    @DisplayName("endOngoingVisit() inserts a visit record into the visits table")
    void testEndOngoingVisit_2() {
        Student student = student().create();
        OngoingVisit ov = ongoingVisit().set(field(OngoingVisit::getStudentId), student.getStudentId()).create();
        Visit visit = visit()
            .set(field(Visit::getStudentId), student.getStudentId())
            .set(field(Visit::getStartTime), ov.getStartTime())
            .create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_ONGOING_VISIT).bindBean(ov).execute());

        dao.endOngoingVisit(visit);

        List<Visit> dbVisits = jdbi.withHandle(handle ->
            handle.createQuery("SELECT * FROM visits").map(new RowToVisitMapper()).list());
        assertFalse(dbVisits.isEmpty());
        Visit inserted = dbVisits.get(0);
        assertEquals(visit.getStudentId(), inserted.getStudentId());
        assertEquals(visit.getStartTime(), inserted.getStartTime());
        assertEquals(visit.getDuration(), inserted.getDuration());
    }

    @Test
    @DisplayName("endOngoingVisit() returns the generated visit ID")
    void testEndOngoingVisit_3() {
        Student student = student().create();
        OngoingVisit ov = ongoingVisit().set(field(OngoingVisit::getStudentId), student.getStudentId()).create();
        Visit visit = visit()
            .set(field(Visit::getStudentId), student.getStudentId())
            .set(field(Visit::getStartTime), ov.getStartTime())
            .create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(student).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_ONGOING_VISIT).bindBean(ov).execute());

        int returnedId = dao.endOngoingVisit(visit);

        Optional<Visit> dbVisit = jdbi.withHandle(handle ->
            handle.createQuery("SELECT * FROM visits WHERE visit_id = ?")
            .bind(0, returnedId).map(new RowToVisitMapper()).findOne());
        assertTrue(dbVisit.isPresent());
    }

    @Test
    @DisplayName("endOngoingVisit() rolls back deleteOngoingVisit if insertVisit fails")
    void testEndOngoingVisit_4() {
        // Set up a student and an ongoing visit. Pass a malformed Visit to endOngoingVisit.
        Integer studentId = gen().ints().min(1).get();
        Student s = student().set(field(Student::getStudentId), studentId).create();
        OngoingVisit ov = ongoingVisit().set(field(OngoingVisit::getStudentId), studentId).create();
        Visit v = visit()
                .set(field(Visit::getDuration), null)
                .set(field(Visit::getStudentId), studentId)
                .create();
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_STUDENT).bindBean(s).execute());
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_ONGOING_VISIT).bindBean(ov).execute());

        try {
            dao.endOngoingVisit(v);
        } catch (UnableToExecuteStatementException e) {
            // do nothing
        }

        // Expected: insertVisit fails, transaction rolls back, ongoing visit is still in the table.
        OngoingVisit result = jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM ongoing_visits WHERE student_id = ?")
                    .bind(0, studentId).map(new RowToOngoingVisitMapper()).one());

        assertEquals(studentId, result.getStudentId());
    }
}
