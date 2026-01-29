package com.github.unchangingconstant.studenttracker.app.dbmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.instancio.Select.field;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import com.github.unchangingconstant.studenttracker.app.entities.EntityTestUtil;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.unchangingconstant.studenttracker.app.entities.Student;
import com.github.unchangingconstant.studenttracker.app.dbmanager.DatabaseManager.InvalidEntityException;

/* TODO
 * Figure out how you'll test the Observer system. Should it be a different aspect of the program?
 * Or should it be a more separate component?
 */
public class DatabaseManagerTest {

    @Mock
    private AttendanceDAO dao;

    @InjectMocks
    private DatabaseManager manager;

    private Boolean triggered;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        triggered = false;
    }

    @Test
    @DisplayName("Service returns student if DAO was able to find the student with the given ID")
    void testFindStudent_1() throws Exception {
        Optional<Student> expected = Optional.of(EntityTestUtil.student().create());
        int studentId = expected.get().getStudentId();
        when(dao.findStudent(studentId)).thenReturn(expected);

        assertEquals(expected, manager.findStudent(expected.get().getStudentId()));
    }
    
    @Test
    @DisplayName("Service returns empty optional if DAO was not able to find the student with the given ID")
    void testGetStudent_2() {
        Integer studentId = Instancio.gen().ints().min(1).get();
        when(dao.findStudent(studentId)).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), manager.findStudent(studentId));
    }

    @Test
    @DisplayName("Returns <studentId, student> map if DAO is successful in retrieving all students")
    void testGetAllStudents_1() {
        // TODO make integers match studentIds
        Map<Integer, Student> students = Instancio.ofMap(Integer.class, Student.class).create();
        when(dao.getAllStudents()).thenReturn(students);

        assertEquals(students, manager.getAllStudents());
    }

    @Test
    @DisplayName("Returns empty map if DAO returns empty map")
    void testGetAllStudents_2() {
        Map<Integer, Student> emptyMap = Instancio.ofMap(Integer.class, Student.class).size(0).create();
        when(dao.getAllStudents()).thenReturn(emptyMap);

        assertTrue(manager.getAllStudents().isEmpty());
    }

    @Test
    @DisplayName("Encounters no errors if inserted student is valid")
    void testInsertStudent_1() throws Exception {
        Student expected = EntityTestUtil.validStudent().create();
        when(dao.insertStudent(expected)).thenReturn(expected.getStudentId());

        manager.getStudentsObserver().subscribeToInserts(studentId -> trigger());

        manager.insertStudent(expected);
        assertTrue(triggered); // Some of this weirdness will go on while we test event triggers
    }

    @Test
    @DisplayName("Throws error when fullName invalid")
    void testInsertStudent_2() {
        Student longNameStudent = EntityTestUtil.validStudent()
            .generate(field(Student::getFullName), gen -> gen.string().minLength(151))
            .create();
        manager.getStudentsObserver().subscribeToInserts(studentId -> trigger());
        
        assertThrows(InvalidEntityException.class, () -> manager.insertStudent(longNameStudent));
        assertFalse(triggered);
    }

    @Test
    @DisplayName("Throws error when prefName invalid")
    void testInsertStudent_3() {
        Student longNameStudent = EntityTestUtil.validStudent()
            .generate(field(Student::getPreferredName), gen -> gen.string().minLength(151))
            .create();
        manager.getStudentsObserver().subscribeToInserts(studentId -> trigger());
        
        assertThrows(InvalidEntityException.class, () -> manager.insertStudent(longNameStudent));
        assertFalse(triggered);
    }

    // @Test
    // @DisplayName("Throws error when subjects invalid")
    // void testInsertStudent_4() {
    //     Student s1 = EntityTestUtil.validStudent()
    //         .generate(field(Student::getVisitTime), gen -> gen.ints().max(0))
    //         .create();
    //     Student s2 = EntityTestUtil.validStudent()
    //         .generate(field(Student::getVisitTime), gen -> gen.ints().min(3))
    //         .create();
    //     Student s3 = EntityTestUtil.validStudent()
    //         .set(field(Student::getVisitTime), null)
    //         .create();
    //     service.getStudentsObserver().subscribeToInserts(studentId -> trigger());
        
    //     assertThrows(InvalidEntityException.class,
    //         () -> service.insertStudent(s1.getFullName(), s1.getPrefName(), s1.getVisitTime()));
    //     assertFalse(triggered);

    //     triggered = false;

    //     assertThrows(InvalidEntityException.class,
    //         () -> service.insertStudent(s2.getFullName(), s2.getPrefName(), s2.getVisitTime()));
    //     assertFalse(triggered);

    //     triggered = false;

    //     assertThrows(InvalidEntityException.class,
    //         () -> service.insertStudent(s3.getFullName(), s3.getPrefName(), s3.getVisitTime()));
    //     assertFalse(triggered);
    // }

    // TODO you need to write tests to ensure this sends the proper deleted
    // TODO visits to the observers
    @Test
    @DisplayName("Student is successfully deleted if it's in the database")
    void testDeleteStudent_1()    {
        Integer deleted = Instancio.gen().ints().min(1).get();
        when(dao.deleteStudent(deleted)).thenReturn(true);
        manager.getStudentsObserver().subscribeToDeletes((studentId) -> trigger());

        manager.deleteStudent(deleted);
        assertTrue(triggered);
    }

    @Test
    @DisplayName("NoSuchElementException is thrown if nothing was deleted")
    void testDeleteStudent_2()  {

    }

    @Test
    @DisplayName("NoSuchEntityException is throw if Student has visits in the database")
    void testDeleteStudent_3() {

    }

    @Test
    @DisplayName("Student is updated if it's valid and exists in the database")
    void testUpdateStudent_1()  {

    }

    @Test
    @DisplayName("NoSuchElementException is thrown if student with passed studentId doesn't exist")
    void testUpdateStudent_2()  {
        
    }

    @Test
    @DisplayName("Throws error if new fullLegalName is invalid")
    void testUpdateStudent_3()  {

    }

    @Test
    @DisplayName("Throws error if new prefName is invalid")
    void testUpdateStudent_4()  {

    }

    @Test
    @DisplayName("Throws error if new subjects is invalid") 
    void testUpdateStudent_5() {

    }


    /**
     * HELPER METHODS
     */
    private void  trigger()    {
        triggered = true;
    }

}
