package com.github.unchangingconstant.studenttracker.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.instancio.Select.field;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.NoSuchElementException;

import com.github.unchangingconstant.studenttracker.app.dbmanager.AttendanceRecordManager;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.unchangingconstant.studenttracker.app.dbmanager.AttendanceDAO;
import com.github.unchangingconstant.studenttracker.app.entities.Student;
import com.github.unchangingconstant.studenttracker.app.domain.StudentTestUtil;
import com.github.unchangingconstant.studenttracker.app.dbmanager.AttendanceRecordManager.IllegalDatabaseOperationException;
import com.github.unchangingconstant.studenttracker.app.dbmanager.AttendanceRecordManager.InvalidEntityException;

public class AttendanceRecordManagerTest {

    @Mock
    private AttendanceDAO dao;

    @InjectMocks
    private AttendanceRecordManager service;

    private Boolean triggered;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        triggered = false;
    }

    @Test
    @DisplayName("Service returns student if DAO was able to find the student with the given ID")
    void testGetStudent_1() throws Exception {
        Student s = StudentTestUtil.student().create();
        when(dao.findStudent(s.getStudentId())).thenReturn(s);

        assertEquals(s, service.findStudent(s.getStudentId()));
    }
    
    @Test
    @DisplayName("Service throws exception if DAO was not able to find the student with the given ID")
    void testGetStudent_2() {
        Integer studentId = Instancio.gen().ints().min(1).get();
        when(dao.findStudent(studentId)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> service.findStudent(studentId));
    }

    @Test
    @DisplayName("Returns studentId, student maps if DAO is successful in retrieving all students")
    void testGetAllStudents_1() {
        // TODO make integers match studentIds
        Map<Integer, Student> students = Instancio.ofMap(Integer.class, Student.class).create();
        when(dao.getAllStudents()).thenReturn(students);

        assertEquals(students, service.getAllStudents());
    }

    @Test
    @DisplayName("Returns empty map if DAO returns empty map")
    void testGetAllStudents_2() {
        Map<Integer, Student> emptyMap = Instancio.ofMap(Integer.class, Student.class).size(0).create();
        when(dao.getAllStudents()).thenReturn(emptyMap);

        assertEquals(0, service.getAllStudents().size());
    }

    @Test
    @DisplayName("Encounters no errors if inserted student is valid")
    void testInsertStudent_1() throws Exception {
        Student s = StudentTestUtil.validStudent().create();
        when(dao.insertStudent(s.getFullLegalName(), 
            s.getPrefName(), s.getVisitTime(), s.getDateAdded()))
            .thenReturn(s.getStudentId());
        service.getStudentsObserver().subscribeToInserts(studentId -> trigger());
        
        service.insertStudent(s.getFullLegalName(), s.getPrefName(), s.getVisitTime());
        assertTrue(triggered); // Some of this weirdness will go on while we test event triggers
    }

    @Test
    @DisplayName("Throws error when fullLegalName invalid")
    void testInsertStudent_2() {
        Student s1 = StudentTestUtil.validStudent()
            .generate(field(Student::getFullLegalName), gen -> gen.string().minLength(151))
            .create();
        Student s2 = StudentTestUtil.validStudent()
            .generate(field(Student::getFullLegalName), gen -> gen.string().maxLength(0))
            .create();
        service.getStudentsObserver().subscribeToInserts(studentId -> trigger());
        
        assertThrows(InvalidEntityException.class,
            () -> service.insertStudent(s1.getFullLegalName(), s1.getPrefName(), s1.getVisitTime()));
        assertFalse(triggered);

        triggered = false;

        assertThrows(InvalidEntityException.class,
            () -> service.insertStudent(s2.getFullLegalName(), s2.getPrefName(), s2.getVisitTime()));
        assertFalse(triggered);
    }

    @Test
    @DisplayName("Throws error when prefName invalid")
    void testInsertStudent_3() {
        Student s = StudentTestUtil.validStudent()
            .generate(field(Student::getPrefName), gen -> gen.string().minLength(151))
            .create();
        service.getStudentsObserver().subscribeToInserts(studentId -> trigger());
        
        assertThrows(InvalidEntityException.class,
            () -> service.insertStudent(s.getFullLegalName(), s.getPrefName(), s.getVisitTime()));
        assertFalse(triggered);
    }

    // @Test
    // @DisplayName("Throws error when subjects invalid")
    // void testInsertStudent_4() {
    //     Student s1 = StudentTestUtil.validStudent()
    //         .generate(field(Student::getSubjects), gen -> gen.ints().max(0))
    //         .create();
    //     Student s2 = StudentTestUtil.validStudent()
    //         .generate(field(Student::getSubjects), gen -> gen.ints().min(3))
    //         .create();
    //     Student s3 = StudentTestUtil.validStudent()
    //         .set(field(Student::getSubjects), null)
    //         .create();
    //     service.getStudentsObserver().subscribeToInserts(studentId -> trigger());
        
    //     assertThrows(InvalidEntityException.class,
    //         () -> service.insertStudent(s1.getFullLegalName(), s1.getPrefName(), s1.getSubjects()));
    //     assertFalse(triggered);

    //     triggered = false;

    //     assertThrows(InvalidEntityException.class,
    //         () -> service.insertStudent(s2.getFullLegalName(), s2.getPrefName(), s2.getSubjects()));
    //     assertFalse(triggered);

    //     triggered = false;

    //     assertThrows(InvalidEntityException.class,
    //         () -> service.insertStudent(s3.getFullLegalName(), s3.getPrefName(), s3.getSubjects()));
    //     assertFalse(triggered);
    // }

    @Test
    @DisplayName("Student is successfully deleted if it's in the database")
    void testDeleteStudent_1() throws IllegalDatabaseOperationException    {
        Integer deleted = Instancio.gen().ints().min(1).get();
        when(dao.deleteStudent(deleted)).thenReturn(true);
        service.getStudentsObserver().subscribeToDeletes((studentId) -> trigger());

        service.deleteStudent(deleted);
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
    private void trigger()    {
        triggered = true;
    }

}
