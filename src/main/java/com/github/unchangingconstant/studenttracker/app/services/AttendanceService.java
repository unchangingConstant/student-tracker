package com.github.unchangingconstant.studenttracker.app.services;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

import com.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;
import com.github.unchangingconstant.studenttracker.app.domain.OngoingVisitDomain;
import com.github.unchangingconstant.studenttracker.app.domain.StudentDomain;
import com.github.unchangingconstant.studenttracker.app.domain.VisitDomain;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import lombok.Getter;

@Singleton
public class AttendanceService {

    private DatabaseDAO dao;

    @Getter
    private AttendanceObserver<OngoingVisitDomain> ongoingVisitsObserver;
    @Getter
    private AttendanceObserver<VisitDomain> visitsObserver;
    @Getter
    private AttendanceObserver<StudentDomain> studentsObserver;

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

    public StudentDomain getStudent(Integer studentId) {
        StudentDomain result = dao.getStudent(studentId);
        if (result == null) {
            throw new NoSuchElementException(String.format("Student with studentId %d does not exist in database", studentId));
        }
        return result;
    }

    public synchronized Map<Integer, StudentDomain> getAllStudents() {
        return dao.getAllStudents();
    }

    public void insertStudent(String fullLegalName, String prefName, Integer subjects) throws InvalidDatabaseEntryException {
        StudentDomain validStudent = validateStudent(fullLegalName, prefName, subjects);
        Instant dateAdded = Instant.now();
        Integer studentId = dao.insertStudent(validStudent.getFullLegalName(), validStudent.getPrefName(), subjects, dateAdded);
        validStudent.setStudentId(studentId);
        validStudent.setDateAdded(dateAdded);
        studentsObserver.triggerInsert(validStudent);
    }

    public void deleteStudent(Integer studentId) throws IllegalDatabaseOperationException {
        // First deletes visits
        List<VisitDomain> deletedVisits = dao.getStudentVisits(studentId); 
        dao.deleteStudentVisits(studentId);
        visitsObserver.triggerDelete(deletedVisits);

        // Then deletes students
        // TODO refactor? Check if the try-catch block is necessary after the issue 17 change
        try {
            if (dao.deleteStudent(studentId))   {
                studentsObserver.triggerDelete(StudentDomain.builder().studentId(studentId).build());
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

    public void updateStudent(Integer studentId, String fullLegalName, String prefName, Integer subjects) throws InvalidDatabaseEntryException    {
        StudentDomain validUpdate = validateStudent(fullLegalName, prefName, subjects);
        Integer updated = dao.updateStudent(validUpdate.getFullLegalName(), validUpdate.getPrefName(), subjects, studentId);
        if (updated == 1)   {
            validUpdate.setStudentId(studentId);
            studentsObserver.triggerUpdate(validUpdate);
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
        return dao.getVisit(visitId);
    }

    public void insertVisit(Integer studentId, Instant startTime, Instant endTime)  {
        Integer visitId = dao.insertVisit(startTime, endTime, studentId);
        VisitDomain visit = VisitDomain.builder().visitId(visitId).studentId(studentId).startTime(startTime).endTime(endTime).build();
        visitsObserver.triggerInsert(visit);
    }

    public void deleteVisit(Integer visitId)   {
        dao.deleteVisit(visitId); // TODO wtf is this
        VisitDomain visit = VisitDomain.builder().visitId(visitId).build();
        visitsObserver.triggerDelete(visit);
    }

    public List<VisitDomain> getStudentVisits(Integer studentId) {
        return dao.getStudentVisits(studentId);
    }

    /*
     * ONGOING VISIT METHODS
     */

    public Map<Integer, OngoingVisitDomain> getOngoingVisits() {
        return dao.getOngoingVisits();
    }

    // Returns null if nothing found TODO returning null better than throwing exception in some cases
    // Fix other service methods
    public OngoingVisitDomain getOngoingVisit(Integer studentId)   {
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
        OngoingVisitDomain newOngoingVisit = dao.getOngoingVisit(studentId);
        ongoingVisitsObserver.triggerInsert(
            OngoingVisitDomain.builder()
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
        ongoingVisitsObserver.triggerDelete(OngoingVisitDomain.builder().studentId(studentId).build());
        // Logs endtime into Visit table
        Instant endTime = Instant.now();
        Integer visitId = dao.insertVisit(startTime, endTime, studentId);
        visitsObserver.triggerInsert(VisitDomain.builder().visitId(visitId).studentId(studentId).startTime(startTime).endTime(endTime).build());
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

    /**
     * HELPERS
     */
    private StudentDomain validateStudent(String fullLegalName, String prefName, Integer subjects)  throws InvalidDatabaseEntryException {
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

        return StudentDomain.builder().fullLegalName(trimmedFullName).prefName(trimmedPrefName).subjects(subjects).build();
    }

}
