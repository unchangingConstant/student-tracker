package com.github.unchangingconstant.studenttracker.app.services;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Path.Node;
import javax.validation.Validation;

import com.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;
import com.github.unchangingconstant.studenttracker.app.entities.OngoingVisit;
import com.github.unchangingconstant.studenttracker.app.entities.Student;
import com.github.unchangingconstant.studenttracker.app.entities.Visit;
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

    public Optional<Student> findStudent(Integer studentId) {
        return dao.findStudent(studentId);
    }

    public List<Student> getAllStudents() {
        return dao.getAllStudents();
    }

    public void insertStudent(Student student) throws InvalidEntityException {
        if (!validateEntityExcept(student, Set.of("studentId")).isEmpty()) {
            throw new InvalidEntityException();
        }
        Integer studentId = dao.insertStudent(student);
        Student newStudent = Student.builder()
            .studentId(studentId)
            .fullName(student.getFullName())
            .preferredName(student.getPreferredName())
            .visitTime(student.getVisitTime())
            .dateAdded(student.getDateAdded()).build();
        studentsObserver.triggerInsert(newStudent);
    }

    public void deleteStudent(Integer studentId) {
        if (dao.deleteStudent(studentId))   {
            studentsObserver.triggerDelete(Student.builder().studentId(studentId).build());
            return;
        }
        throw new NoSuchEntityException(studentId);
    }

    public void updateStudent(Student student) throws InvalidEntityException {
        if (!validateEntity(student).isEmpty()) {
            throw new InvalidEntityException();
        };
        boolean updated = dao.updateStudent(student);
        if (updated)   {
            studentsObserver.triggerUpdate(student);
        }
        else {
            throw new NoSuchEntityException(student.getStudentId());
        }
    }

    /*
     * VISIT METHODS
     */

    public Visit getVisit(Integer visitId) {
        return dao.findVisit(visitId);
    }

    public void insertVisit(Visit visit) throws InvalidEntityException {
        if (!validateEntityExcept(visit, Set.of("visitId")).isEmpty()) {
            throw new InvalidEntityException();
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
        return dao.findVisitsWithStudentId(studentId);
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
    static public class InvalidEntityException extends Exception {
        public InvalidEntityException()  {super();}
    }

    static public class NoSuchEntityException extends NoSuchElementException {
        public NoSuchEntityException(Integer id) {
            super("Entity with unique identifier " + String.valueOf(id) + " could not be found.");
        }
    }

    /**
     * HELPERS
     */
    private Set<ConstraintViolation<Object>> validateEntity(Object entity) {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(entity);
    }

    // Ignores constraint violations for specified properties
    private Set<ConstraintViolation<Object>> validateEntityExcept(Object entity, Set<String> propertyExceptions) throws InvalidEntityException {
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
