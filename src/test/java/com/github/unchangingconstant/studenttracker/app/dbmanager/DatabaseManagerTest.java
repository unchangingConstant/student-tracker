package com.github.unchangingconstant.studenttracker.app.dbmanager;

import static com.github.unchangingconstant.studenttracker.app.entities.EntityTestUtil.*;
import static org.instancio.Instancio.gen;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.github.unchangingconstant.studenttracker.app.entities.EntityTestUtil;
import com.github.unchangingconstant.studenttracker.app.entities.OngoingVisit;
import com.github.unchangingconstant.studenttracker.app.entities.Visit;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.unchangingconstant.studenttracker.app.entities.Student;
import com.github.unchangingconstant.studenttracker.app.dbmanager.DatabaseManager.InvalidEntityException;
import com.github.unchangingconstant.studenttracker.app.dbmanager.DatabaseManager.NoSuchEntityException;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

/* TODO
 * Figure out how you'll test the Observer system. Should it be a different aspect of the program?
 * Or should it be a more separate component?
 * TODO
 *  Test that the observer is called with the CORRECT ARGUMENTS as well
 */
@ExtendWith(MockitoExtension.class)
public class DatabaseManagerTest {

    @Mock
    private AttendanceDAO dao;
    @Mock
    private DatabaseObserver<OngoingVisit> ongoingVisitsObserver;
    @Mock
    private DatabaseObserver<Visit> visitsObserver;
    @Mock
    private DatabaseObserver<Student> studentsObserver;

    // @TriggerMocks won't work, sadge. Manual injection in @BeforeEach method
    private DatabaseManager manager;

    @BeforeEach
    void setUp() {
        manager = new DatabaseManager(dao, ongoingVisitsObserver, visitsObserver, studentsObserver);
    }

    @Test
    @DisplayName("Returns student if DAO was able to find the student with the given ID")
    void testFindStudent_1() throws Exception {
        Optional<Student> expected = Optional.of(validStudent().create());
        int studentId = expected.get().getStudentId();
        when(dao.findStudent(studentId)).thenReturn(expected);

        assertEquals(expected, manager.findStudent(expected.get().getStudentId()));
    }

    @Test
    @DisplayName("Returns empty optional if DAO was not able to find the student with the given ID")
    void testGetStudent_2() {
        Integer studentId = gen().ints().min(1).get();
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
    @DisplayName("Observers are called properly if inserted student is valid")
    void testInsertStudent_1() throws InvalidEntityException {
        Student expected = EntityTestUtil.validStudent()
                .generate(field(Student::getStudentId), gen -> gen().ints().min(1))
                .create();
        when(dao.insertStudent(expected)).thenReturn(expected.getStudentId());

        manager.insertStudent(expected);

        verify(dao).insertStudent(expected);
        verify(studentsObserver).triggerInsert(expected);
    }

    @Test
    @DisplayName("Throws InvalidEntityException and doesn't notify observers when Student is invalid")
    void testInsertStudent_2() {
        Student longNameStudent = EntityTestUtil.validStudent()
            .generate(field(Student::getFullName), gen -> gen.string().minLength(Student.FULL_NAME_MAX_LEN + 1))
            .create();

        assertThrows(InvalidEntityException.class, () -> manager.insertStudent(longNameStudent));
        verify(studentsObserver, times(0)).triggerInsert(any(Student.class));
    }

    @Test
    @DisplayName("Notifies observers if Student is successfully deleted")
    void testDeleteStudent_1()    {
        Integer deleted = gen().ints().min(1).get();
        List<Visit> studentVisits = Instancio.ofList(validVisit().set(field(Visit::getStudentId), deleted).toModel()).create();
        when(dao.findVisitsWithStudentId(deleted)).thenReturn(studentVisits);
        when(dao.deleteStudent(deleted)).thenReturn(true);

        manager.deleteStudent(deleted);

        verify(visitsObserver).triggerDelete(studentVisits);
        verify(studentsObserver).triggerDelete(Student.builder().studentId(deleted).build());
    }

    @Test
    @DisplayName("NoSuchEntityException is thrown if nothing was deleted")
    void testDeleteStudent_2()  {
        Integer studentId = gen().ints().min(1).get();
        when(dao.findVisitsWithStudentId(studentId)).thenReturn(List.of());
        when(dao.deleteStudent(studentId)).thenReturn(false);

        assertThrows(NoSuchEntityException.class, () -> manager.deleteStudent(studentId));
        verify(visitsObserver, times(0)).triggerDelete(anyList());
        verify(studentsObserver, times(0)).triggerDelete(any(Student.class));
    }

    @Test
    @DisplayName("Observer is triggered if valid Student that exists in the database is passed")
    void testUpdateStudent_1() throws InvalidEntityException {
        Student student = validStudent().create();
        when(dao.updateStudent(student)).thenReturn(true);

        manager.updateStudent(student);

        verify(studentsObserver).triggerUpdate(student);
        verify(visitsObserver, times(0)).triggerUpdate(any(Visit.class));
        verify(ongoingVisitsObserver, times(0)).triggerUpdate(any(OngoingVisit.class));
    }

    @Test
    @DisplayName("NoSuchEntityException is thrown if student with passed studentId doesn't exist")
    void testUpdateStudent_2()  {
        Student student = validStudent().create();
        when(dao.updateStudent(student)).thenReturn(false);

        assertThrows(NoSuchEntityException.class, () -> manager.updateStudent(student));
        verify(studentsObserver, times(0)).triggerUpdate(any(Student.class));
    }

    @Test
    @DisplayName("Throws InvalidEntityException if new Student is invalid")
    void testUpdateStudent_3()  {
        Student longNameStudent = validStudent()
            .generate(field(Student::getFullName), gen -> gen.string().minLength(Student.FULL_NAME_MAX_LEN + 1))
            .create();

        assertThrows(InvalidEntityException.class, () -> manager.updateStudent(longNameStudent));
        verify(dao, times(0)).updateStudent(any(Student.class));
        verify(studentsObserver, times(0)).triggerUpdate(any(Student.class));
    }

    @Test
    @DisplayName("Throws InvalidEntityException if new Student's studentId is null")
    void testUpdateStudent_4() {
        Student nullIdStudent = validStudent().set(field(Student::getStudentId), null).create();

        assertThrows(InvalidEntityException.class, () -> manager.updateStudent(nullIdStudent));
        verify(dao, times(0)).updateStudent(any(Student.class));
        verify(studentsObserver, times(0)).triggerUpdate(any(Student.class));
    }

    @Test
    @DisplayName("insertVisit() notifies visitsObserver with correct Visit on valid insert")
    void testInsertVisit_1() throws InvalidEntityException {
        Visit visit = validVisit().create();
        Integer visitId = gen().ints().min(1).get();
        when(dao.insertVisit(visit)).thenReturn(visitId);

        manager.insertVisit(visit);

        Visit expectedVisit = Visit.builder()
            .visitId(visitId)
            .startTime(visit.getStartTime())
            .duration(visit.getDuration())
            .studentId(visit.getStudentId()).build();
        verify(dao).insertVisit(visit);
        verify(visitsObserver).triggerInsert(expectedVisit);
        verify(studentsObserver, times(0)).triggerInsert(any(Student.class));
        verify(ongoingVisitsObserver, times(0)).triggerInsert(any(OngoingVisit.class));
    }

    @Test
    @DisplayName("insertVisit() throws InvalidEntityException and doesn't notify observers on invalid Visit")
    void testInsertVisit_2() {
        Visit invalidVisit = visit().set(field(Visit::getStudentId), null).create();

        assertThrows(InvalidEntityException.class, () -> manager.insertVisit(invalidVisit));
        verify(dao, times(0)).insertVisit(any(Visit.class));
        verify(visitsObserver, times(0)).triggerInsert(any(Visit.class));
    }

    @Test
    @DisplayName("deleteVisit() notifies visitsObserver on successful delete")
    void testDeleteVisit_1() {
        Integer visitId = gen().ints().min(1).get();
        when(dao.deleteVisit(visitId)).thenReturn(true);

        manager.deleteVisit(visitId);

        Visit expectedVisit = Visit.builder().visitId(visitId).build();
        verify(dao).deleteVisit(visitId);
        verify(visitsObserver).triggerDelete(expectedVisit);
        verify(studentsObserver, times(0)).triggerDelete(any(Student.class));
        verify(ongoingVisitsObserver, times(0)).triggerDelete(any(OngoingVisit.class));
    }

    @Test
    @DisplayName("deleteVisit() throws NoSuchElementException and doesn't notify observers on failed delete")
    void testDeleteVisit_2() {
        Integer visitId = gen().ints().min(1).get();
        when(dao.deleteVisit(visitId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> manager.deleteVisit(visitId));
        verify(visitsObserver, times(0)).triggerDelete(any(Visit.class));
    }

    @Test
    @DisplayName("findVisitsWithStudentId() returns list of visits from DAO")
    void testFindVisitsWithStudentId_1() {
        Integer studentId = gen().ints().min(1).get();
        List<Visit> visits = Instancio.createList(Visit.class);
        when(dao.findVisitsWithStudentId(studentId)).thenReturn(visits);

        assertEquals(visits, manager.findVisitsWithStudentId(studentId));
    }

    @Test
    @DisplayName("findVisitsWithStudentId() returns empty list if DAO returns empty list")
    void testFindVisitsWithStudentId_2() {
        Integer studentId = gen().ints().min(1).get();
        when(dao.findVisitsWithStudentId(studentId)).thenReturn(List.of());

        assertTrue(manager.findVisitsWithStudentId(studentId).isEmpty());
    }

    @Test
    @DisplayName("getOngoingVisits() returns <studentId, OngoingVisit> map from DAO")
    void testGetOngoingVisits_1() {
        Map<Integer, OngoingVisit> ongoingVisits = Instancio.ofMap(Integer.class, OngoingVisit.class).create();
        when(dao.getOngoingVisits()).thenReturn(ongoingVisits);

        assertEquals(ongoingVisits, manager.getOngoingVisits());
    }

    @Test
    @DisplayName("getOngoingVisits() returns empty map if DAO returns empty map")
    void testGetOngoingVisits_2() {
        Map<Integer, OngoingVisit> emptyMap = Instancio.ofMap(Integer.class, OngoingVisit.class).size(0).create();
        when(dao.getOngoingVisits()).thenReturn(emptyMap);

        assertTrue(manager.getOngoingVisits().isEmpty());
    }

    @Test
    @DisplayName("findOngoingVisit() returns present Optional if student has an ongoing visit")
    void testFindOngoingVisit_1() {
        Integer studentId = gen().ints().min(1).get();
        OngoingVisit ov = ongoingVisit().set(field(OngoingVisit::getStudentId), studentId).create();
        when(dao.findOngoingVisit(studentId)).thenReturn(Optional.of(ov));

        assertEquals(Optional.of(ov), manager.findOngoingVisit(studentId));
    }

    @Test
    @DisplayName("findOngoingVisit() returns empty Optional if student has no ongoing visit")
    void testFindOngoingVisit_2() {
        Integer studentId = gen().ints().min(1).get();
        when(dao.findOngoingVisit(studentId)).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), manager.findOngoingVisit(studentId));
    }

    @Test
    @DisplayName("startOngoingVisit() notifies ongoingVisitsObserver and calls DAO on insert")
    void testStartOngoingVisit_1() throws InvalidEntityException {
        OngoingVisit ongoingVisit = validOngoingVisit().create();

        manager.startOngoingVisit(ongoingVisit);

        verify(dao).insertOngoingVisit(ongoingVisit);
        verify(ongoingVisitsObserver).triggerInsert(ongoingVisit);
        verify(visitsObserver, times(0)).triggerInsert(any(Visit.class));
        verify(studentsObserver, times(0)).triggerInsert(any(Student.class));
    }

    @Test
    @DisplayName("endOngoingVisit() triggers ongoingVisitsObserver.triggerDelete() with the original OngoingVisit")
    void testEndOngoingVisit_1() throws InvalidEntityException {
        OngoingVisit ongoingVisit = validOngoingVisit().create();
        Integer duration = gen().ints().min(Visit.MIN_DURATION).get();
        Integer visitId = gen().ints().min(1).get();
        when(dao.endOngoingVisit(any(Visit.class))).thenReturn(visitId);

        manager.endOngoingVisit(ongoingVisit, duration);

        verify(ongoingVisitsObserver).triggerDelete(ongoingVisit);
        verify(studentsObserver, times(0)).triggerDelete(any(Student.class));
    }

    @Test
    @DisplayName("endOngoingVisit() triggers visitsObserver.triggerInsert() with correctly built Visit")
    void testEndOngoingVisit_2() throws InvalidEntityException {
        OngoingVisit ongoingVisit = validOngoingVisit().create();
        Integer duration = gen().ints().min(Visit.MIN_DURATION).get();
        Integer visitId = gen().ints().min(1).get();
        when(dao.endOngoingVisit(any(Visit.class))).thenReturn(visitId);

        manager.endOngoingVisit(ongoingVisit, duration);

        Visit expectedVisit = Visit.builder()
            .visitId(visitId)
            .studentId(ongoingVisit.getStudentId())
            .startTime(ongoingVisit.getStartTime())
            .duration(duration).build();
        verify(visitsObserver).triggerInsert(expectedVisit);
        verify(studentsObserver, times(0)).triggerInsert(any(Student.class));
    }

    @Test
    @DisplayName("endOngoingVisit() throws InvalidEntityException if constructed Visit is invalid")
    void testEndOngoingVisit_3() {
        OngoingVisit ongoingVisit = validOngoingVisit().create();

        assertThrows(InvalidEntityException.class, () -> manager.endOngoingVisit(ongoingVisit, null));
        verify(dao, times(0)).endOngoingVisit(any(Visit.class));
        verify(ongoingVisitsObserver, times(0)).triggerDelete(any(OngoingVisit.class));
        verify(visitsObserver, times(0)).triggerInsert(any(Visit.class));
    }

}
