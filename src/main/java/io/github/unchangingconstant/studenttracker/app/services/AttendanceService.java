package io.github.unchangingconstant.studenttracker.app.services;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;
import io.github.unchangingconstant.studenttracker.app.domain.OngoingVisitDomain;
import io.github.unchangingconstant.studenttracker.app.domain.StudentDomain;
import io.github.unchangingconstant.studenttracker.app.domain.VisitDomain;
import lombok.Getter;

@Singleton
public class AttendanceService {

    private DatabaseDAO dao;

    @Getter
    private Observer<Integer, OngoingVisitDomain> ongoingVisitsObserver;
    @Getter
    private Observer<Integer, VisitDomain> visitsObserver;
    @Getter
    private Observer<Integer, StudentDomain> studentsObserver;

    @Inject
    public AttendanceService(DatabaseDAO dao)  {
        this.dao = dao;
        this.ongoingVisitsObserver = new Observer<>();
        this.visitsObserver = new Observer<>();
        this.studentsObserver = new Observer<>();
    }

    /*
     * STUDENT METHODS
     */

    public StudentDomain getStudent(Integer studentId) {
        StudentDomain result = dao.getStudent(studentId);
        if (result == null) {
            throw new NoSuchElementException(String.format("Student with studentId %d does not exist in database", studentId));
        }
        return result;
    }

    public Map<Integer, StudentDomain> getAllStudents() {
        return dao.getAllStudents();
    }

    public void insertStudent(String fullLegalName, String prefName, Integer subjects) throws InvalidDatabaseEntryException {
        // Insert students if their full name is less than 150 characters and at least 1 character
        String trimmedFullName = fullLegalName.trim().replaceAll("\\s+", " ");
        String trimmedPrefName = prefName == null ? "" : prefName.trim().replaceAll("\\s+", " ");

        if (trimmedPrefName.length() > 150 || trimmedFullName.length() > 150) {
            throw new InvalidDatabaseEntryException("Names can not be more than 150 characters in length");
        }
        if (trimmedFullName.length() < 1)   {
            throw new InvalidDatabaseEntryException("Names can not be less than 1 character in length");
        }
        if (subjects == null)   {
            throw new InvalidDatabaseEntryException("Subjects is null. Contact developer for help");
        }
        if (subjects != 1 && subjects != 2)  {
            throw new InvalidDatabaseEntryException("Students can only take either 1 or 2 subjects");
        }
        Integer studentId = dao.insertStudent(trimmedFullName, trimmedPrefName, subjects, Instant.now());
        studentsObserver.triggerInsert(studentId);
    }

    public void deleteStudent(Integer studentId) throws IllegalDatabaseOperationException {
        try {
            if (dao.deleteStudent(studentId))   {
                studentsObserver.triggerDelete(studentId);
                return;
            }
            throw new NoSuchElementException();
        } catch (UnableToExecuteStatementException e) {
            // If unable to delete due to foreign key constraints
            if (e.getMessage().contains("FOREIGN KEY constraint")) {
                throw new IllegalDatabaseOperationException("Cannot delete student with existing visits", e);
            }
            throw e;
        }
    }

    public void updateStudent(Integer studentId, String fullLegalName, String prefName, Integer subjects)    {
        Integer updated = dao.updateStudent(fullLegalName, prefName, subjects, studentId);
        if (updated == 1)   {
            studentsObserver.triggerUpdate(StudentDomain.builder()
                .studentId(studentId)
                .fullLegalName(fullLegalName)
                .prefName(prefName)
                .subjects(subjects)
                .build());
            return;
        }
        if (updated == 0)   {
            throw new NoSuchElementException(String.format("Student with studentId %d doesn't exist", studentId));
        }
        throw new IllegalStateException("More than one update occurred on AttendanceService.updateStudent(). Something is seriously wrong");
    }

    /*
     * VISIT METHODS
     */

    public VisitDomain getVisit(Integer visitId) {
        return this.dao.getVisit(visitId);
    }

    public void insertVisit(Integer studentId, Instant startTime, Instant endTime)  {
        Integer visitId = dao.insertVisit(startTime, endTime, studentId);
        visitsObserver.triggerInsert(visitId);
    }

    public void deleteVisit(Integer visitId)   {
        dao.deleteVisit(visitId);
        visitsObserver.triggerDelete(visitId);
    }

    /*
     * ONGOING VISIT METHODS
     */

    public Map<Integer, OngoingVisitDomain> getOngoingVisits() {
        return this.dao.getOngoingVisits();
    }

    public OngoingVisitDomain getOngoingVisit(Integer studentId)   {
        return dao.getOngoingVisit(studentId);
    }

    public void startOngoingVisit(Integer studentId) {
        // Must not have ongoing visits when starting one
        if (dao.getOngoingVisit(studentId) != null ) {
            throw new IllegalStateException("Student is already in the center.");
        };
        dao.insertOngoingVisit(studentId, Instant.now());
        ongoingVisitsObserver.triggerInsert(studentId);
    }

    // update!!! Should return request status
    public void endOngoingVisit(Integer studentId, Instant startTime) {
        // Ends ongoing visit
        dao.deleteOngoingVisit(studentId);
        ongoingVisitsObserver.triggerDelete(studentId);
        // Logs endtime into Visit table
        Integer visitId = dao.insertVisit(startTime, Instant.now(), studentId);
        visitsObserver.triggerInsert(visitId);
    }

    static public class InvalidDatabaseEntryException extends Exception {
        public InvalidDatabaseEntryException()  {super();}
        public InvalidDatabaseEntryException(String errorMsg)  {super(errorMsg);}
    }

    static public class IllegalDatabaseOperationException extends Exception {
        public IllegalDatabaseOperationException() {super();}
        public IllegalDatabaseOperationException(String errorMsg) {super(errorMsg);}
        public IllegalDatabaseOperationException(String errorMsg, Exception e) {super(errorMsg, e);}
    }

    // static public class NoSuchDatabaseElementException extends Exception {
    //     public NoSuchDatabaseElementException() {super();}
    //     public NoSuchDatabaseElementException(String errorMsg) {super(errorMsg);}
    // }

}
