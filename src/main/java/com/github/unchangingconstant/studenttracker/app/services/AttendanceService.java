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
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

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

    /*
     * TODO Consider creating a more robust observer system, to avoid double disk accesses like these
     */
    public void deleteStudent(Integer studentId) {
        List<Visit> studentVisits = dao.findVisitsWithStudentId(studentId);
        if (dao.deleteStudent(studentId))   {
            visitsObserver.triggerDelete(studentVisits);
            studentsObserver.triggerDelete(Student.builder().studentId(studentId).build());
            return;
        }
        throw new NoSuchEntityException(studentId);
    }

    public void updateStudent(Student student) throws InvalidEntityException {
        if (!validateEntity(student).isEmpty()) {
            throw new InvalidEntityException();
        };
        if (dao.updateStudent(student))   {
            studentsObserver.triggerUpdate(student);
        }
        else {
            throw new NoSuchEntityException(student.getStudentId());
        }
    }

    /*
     * VISIT METHODS
     */

    public Optional<Visit> findVisit(Integer visitId) {
        return dao.findVisit(visitId);
    }

    public void insertVisit(Visit visit) throws InvalidEntityException {
        if (!validateEntityExcept(visit, Set.of("visitId")).isEmpty()) {
            throw new InvalidEntityException();
        }
        Integer visitId = dao.insertVisit(visit);
        Visit newVisit = Visit.builder()
            .visitId(visitId)
            .startTime(visit.getStartTime())
            .duration(visit.getDuration())
            .studentId(visit.getStudentId()).build();
        visitsObserver.triggerInsert(newVisit);
    }

    public void deleteVisit(Integer visitId)   {
        if (dao.deleteVisit(visitId)) {
            Visit visit = Visit.builder().visitId(visitId).build();
            visitsObserver.triggerDelete(visit);
        }
        throw new NoSuchElementException();
    }

    public List<Visit> findVisitsWithStudentId(Integer studentId) {
        return dao.findVisitsWithStudentId(studentId);
    }

    /*
     * ONGOING VISIT METHODS
     */

    public List<OngoingVisit> getOngoingVisits() {
        return dao.getOngoingVisits();
    }

    public Optional<OngoingVisit> findOngoingVisit(Integer studentId)   {
        return dao.findOngoingVisit(studentId);
    }

    public void startOngoingVisit(OngoingVisit ongoingVisit) {
        dao.insertOngoingVisit(ongoingVisit);
        ongoingVisitsObserver.triggerInsert(ongoingVisit);
    }

    public void endOngoingVisit(OngoingVisit ongoingVisit, Integer duration) {
        // TODO write database method that ends the ongoing visit in one batch
        dao.deleteOngoingVisit(ongoingVisit.getStudentId());
        ongoingVisitsObserver.triggerDelete(ongoingVisit);
        Visit endedVisit = Visit.builder()
            .studentId(ongoingVisit.getStudentId())
            .startTime(ongoingVisit.getStartTime())
            .duration(duration).build();
        dao.insertVisit(endedVisit);
        visitsObserver.triggerInsert(endedVisit);
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
