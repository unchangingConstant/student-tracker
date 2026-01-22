package com.github.unchangingconstant.studenttracker.app.services;

import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Path.Node;
import javax.validation.Validation;

import com.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;
import com.github.unchangingconstant.studenttracker.app.domain.OngoingVisit;
import com.github.unchangingconstant.studenttracker.app.domain.Student;
import com.github.unchangingconstant.studenttracker.app.domain.Visit;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import lombok.Getter;

@Singleton
public class AttendanceService {

    private final DatabaseDAO dao;

    @Getter
    private final AttendanceObserver<OngoingVisit> ongoingVisitsObserver;
    @Getter
    private final AttendanceObserver<Visit> visitsObserver;
    @Getter
    private final AttendanceObserver<Student> studentsObserver;

    @Inject
    public AttendanceService(DatabaseDAO dao)  {
        this.dao = dao;
        this.ongoingVisitsObserver = new AttendanceObserver<>();
        this.visitsObserver = new AttendanceObserver<>();
        this.studentsObserver = new AttendanceObserver<>();
    }

    /*
     * STUDENT METHODS
     */

    public Student getStudent(Integer studentId) {
        Student result = dao.getStudent(studentId);
        if (result == null) {
            throw new NoSuchElementException(String.format("Student with studentId %d does not exist in database", studentId));
        }
        return result;
    }

    public Map<Integer, Student> getAllStudents() {
        return dao.getAllStudents();
    }

    public void insertStudent(Student student) throws InvalidDatabaseEntryException {
        Instant dateAdded = Instant.now();
        student.setDateAdded(dateAdded);
        if (!validateEntityExcept(student, Set.of("studentId")).isEmpty()) {
            throw new InvalidDatabaseEntryException();
        }
        Integer studentId = dao.insertStudent(student.getFullLegalName(), student.getPrefName(), student.getVisitTime(), dateAdded);
        student.setStudentId(studentId);
        studentsObserver.triggerInsert(student);
    }

    public void deleteStudent(Integer studentId) throws IllegalDatabaseOperationException {
        // First deletes visits
        List<Visit> deletedVisits = dao.getStudentVisits(studentId);
        dao.deleteStudentVisits(studentId);
        visitsObserver.triggerDelete(deletedVisits);

        // Then deletes student
        if (dao.deleteStudent(studentId))   {
            studentsObserver.triggerDelete(Student.builder().studentId(studentId).build());
            return;
        }
        throw new NoSuchElementException();
    }

    public void updateStudent(Student student) throws InvalidDatabaseEntryException    {
        if (!validateEntity(student).isEmpty()) {
            throw new InvalidDatabaseEntryException();
        };

        Integer updated = dao.updateStudent(student.getFullLegalName(), student.getPrefName(), student.getVisitTime(), student.getStudentId());
        if (updated == 1)   {
            studentsObserver.triggerUpdate(student);
            return;
        }
        if (updated == 0)   {
            throw new NoSuchElementException(String.format("Student with studentId %d doesn't exist", student.getStudentId()));
        }
        throw new IllegalStateException("More than one update occurred on AttendanceService.updateStudent(). Something is seriously wrong");
    }

    /*
     * VISIT METHODS
     */

    public Visit getVisit(Integer visitId) {
        return dao.getVisit(visitId);
    }

    public void insertVisit(Visit visit) throws InvalidDatabaseEntryException  {
        if (!validateEntityExcept(visit, Set.of("visitId")).isEmpty()) {
            throw new InvalidDatabaseEntryException();
        }
        Integer visitId = dao.insertVisit(visit.getStartTime(), visit.getEndTime(), visit.getStudentId());
        visit.setVisitId(visitId);
        visitsObserver.triggerInsert(visit);
    }

    public void deleteVisit(Integer visitId)   {
        if (dao.deleteVisit(visitId)) {
            Visit visit = Visit.builder().visitId(visitId).build();
            visitsObserver.triggerDelete(visit);
        }
        throw new NoSuchElementException();
    }

    public List<Visit> getStudentVisits(Integer studentId) {
        return dao.getStudentVisits(studentId);
    }

    /*
     * ONGOING VISIT METHODS
     */

    public Map<Integer, OngoingVisit> getOngoingVisits() {
        return dao.getOngoingVisits();
    }

    // Returns null if nothing found TODO returning null better than throwing exception in some cases
    // Fix other service methods
    public OngoingVisit getOngoingVisit(Integer studentId)   {
        return dao.getOngoingVisit(studentId);
    }

    public void startOngoingVisit(Integer studentId) {
        // Must not have ongoing visits when starting one
        // TODO accessing the database thrice for one operation are we? FIX IT!!!
        if (dao.getOngoingVisit(studentId) != null ) {
            throw new IllegalStateException("Student is already in the center.");
        };
        Instant startTime = Instant.now();
        dao.insertOngoingVisit(studentId, startTime);
        OngoingVisit newOngoingVisit = dao.getOngoingVisit(studentId);
        ongoingVisitsObserver.triggerInsert(
            OngoingVisit.builder()
                .studentId(studentId)
                .startTime(startTime)
                .subjects(newOngoingVisit.getSubjects())
                .studentName(newOngoingVisit.getStudentName())
                .build());
    }

    // update!!! Should return request status
    public void endOngoingVisit(Integer studentId, Instant startTime) {
        // Ends ongoing visit
        dao.deleteOngoingVisit(studentId);
        ongoingVisitsObserver.triggerDelete(OngoingVisit.builder().studentId(studentId).build());
        // Logs endtime into Visit table
        Instant endTime = Instant.now();
        Integer visitId = dao.insertVisit(startTime, endTime, studentId);
        visitsObserver.triggerInsert(Visit.builder().visitId(visitId).studentId(studentId).startTime(startTime).endTime(endTime).build());
    }

    // TODO Make this a Domain object exception, not a database exception. See issue #16 or refer to the pragmatic programmer on DRY
    static public class InvalidDatabaseEntryException extends Exception {
        public InvalidDatabaseEntryException()  {super();}
        public InvalidDatabaseEntryException(String errorMsg)  {super(errorMsg);}
    }

    static public class IllegalDatabaseOperationException extends Exception {
        public IllegalDatabaseOperationException() {super();}
        public IllegalDatabaseOperationException(String errorMsg) {super(errorMsg);}
        public IllegalDatabaseOperationException(String errorMsg, Exception e) {super(errorMsg, e);}
    }

    /**
     * HELPERS
     */
    private Set<ConstraintViolation<Object>> validateEntity(Object entity) throws InvalidDatabaseEntryException {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(entity);
    }

    // Ignores constraint violations for specified properties
    private Set<ConstraintViolation<Object>> validateEntityExcept(Object entity, Set<String> propertyExceptions) throws InvalidDatabaseEntryException {
        Set<ConstraintViolation<Object>> violations = validateEntity(entity);
        return violations.stream().filter(violation -> {
            Node lastNode = null;
            // For the love of god just let me access the field's name directly
            for (Node node : violation.getPropertyPath()) {
                lastNode = node;
            }
            return lastNode != null ? !propertyExceptions.contains(lastNode.getName()) : true;
        }).collect(Collectors.toSet());
    }

}
