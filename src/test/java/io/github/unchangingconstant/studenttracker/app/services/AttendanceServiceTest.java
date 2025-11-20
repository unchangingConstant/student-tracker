package io.github.unchangingconstant.studenttracker.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.NoSuchElementException;

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

public class AttendanceServiceTest {

    @Mock
    private DatabaseDAO dao;

    @InjectMocks
    private AttendanceService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
    @DisplayName("Service return studentId, student maps if DAO is successful in retrieving all students")
    void testGetAllStudents_1() {
        // TODO make integers match studentIds
        Map<Integer, StudentDomain> students = Instancio.ofMap(Integer.class, StudentDomain.class).create();
        when(dao.getAllStudents()).thenReturn(students);

        assertEquals(students, service.getAllStudents());
    }

}
