package io.github.unchangingconstant.studenttracker.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.instancio.Select.field;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;
import io.github.unchangingconstant.studenttracker.app.domain.StudentDomain;
import io.github.unchangingconstant.studenttracker.app.domain.StudentTestUtil;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService.InvalidDatabaseEntryException;

public class AttendanceServiceTest {

    @Mock
    private DatabaseDAO dao;

    @InjectMocks
    private AttendanceService service;

    private Boolean triggered;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        triggered = false;
    }

    @Test
    @DisplayName("Service returns student if DAO was able to find the student with the given ID")
    void testGetStudent_1() throws Exception {
        StudentDomain s = StudentTestUtil.student().create();
        when(dao.getStudent(s.getStudentId())).thenReturn(s);

        assertEquals(s, service.getStudent(s.getStudentId()));
    }
    
    @Test
    @DisplayName("Service throws exception if DAO was not able to find the student with the given ID")
    void testGetStudent_2() {
        Integer studentId = Instancio.create(Integer.class);
        when(dao.getStudent(studentId)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> service.getStudent(studentId));
    }

    @Test
    @DisplayName("Returns studentId, student maps if DAO is successful in retrieving all students")
    void testGetAllStudents_1() {
        // TODO make integers match studentIds
        Map<Integer, StudentDomain> students = Instancio.ofMap(Integer.class, StudentDomain.class).create();
        when(dao.getAllStudents()).thenReturn(students);

        assertEquals(students, service.getAllStudents());
    }

    @Test
    @DisplayName("Returns empty map if DAO returns empty map")
    void testGetAllStudents_2() {
        Map<Integer, StudentDomain> emptyMap = Instancio.ofMap(Integer.class, StudentDomain.class).size(0).create();
        when(dao.getAllStudents()).thenReturn(emptyMap);

        assertEquals(0, service.getAllStudents().size());
    }

    @Test
    @DisplayName("Encounters no errors if inserted student is valid")
    void testInsertStudent_1() throws Exception {
        StudentDomain s = StudentTestUtil.validStudent().create();
        when(dao.insertStudent(s.getFullLegalName(), 
            s.getPrefName(), s.getSubjects(), s.getDateAdded()))
            .thenReturn(s.getStudentId());
        service.getStudentsObserver().subscribeToInserts(studentId -> trigger());
        
        service.insertStudent(s.getFullLegalName(), s.getPrefName(), s.getSubjects());
        assertTrue(triggered); // Some of this weirdness will go on while we test event triggers
    }

    @Test
    @DisplayName("Throws error when fullLegalName invalid")
    void testInsertStudent_2() {
        StudentDomain s1 = StudentTestUtil.validStudent()
            .generate(field(StudentDomain::getFullLegalName), gen -> gen.string().minLength(151))
            .create();
        StudentDomain s2 = StudentTestUtil.validStudent()
            .generate(field(StudentDomain::getFullLegalName), gen -> gen.string().maxLength(0))
            .create();
        service.getStudentsObserver().subscribeToInserts(studentId -> trigger());
        
        assertThrows(InvalidDatabaseEntryException.class, 
            () -> service.insertStudent(s1.getFullLegalName(), s1.getPrefName(), s1.getSubjects()));
        assertFalse(triggered);

        triggered = false;

        assertThrows(InvalidDatabaseEntryException.class, 
            () -> service.insertStudent(s2.getFullLegalName(), s2.getPrefName(), s2.getSubjects()));
        assertFalse(triggered);
    }

    /**
     * HELPER METHODS
     */
    private void trigger()    {
        triggered = true;
    }

}
