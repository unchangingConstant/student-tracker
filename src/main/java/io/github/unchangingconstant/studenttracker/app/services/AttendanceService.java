package io.github.unchangingconstant.studenttracker.app.services;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    public void endOngoingVisit(OngoingVisitDomain ongoingVisit) {
        // Ends ongoing visit
        dao.deleteOngoingVisit(ongoingVisit.getStudentId());
        ongoingVisitsObserver.triggerDelete(ongoingVisit.getStudentId());
        // Logs endtime into Visit table
        Integer visitId = dao.insertVisit(ongoingVisit.getStartTime(), Instant.now(), ongoingVisit.getStudentId());
        visitsObserver.triggerInsert(visitId);
    }

    /*
     * STUDENT METHODS
     */

    public StudentDomain getStudent(Integer studentId) {
        return this.dao.getStudent(studentId);
    }

    public Map<Integer, StudentDomain> getAllStudents() {
        return this.dao.getAllStudents();
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
        if (subjects != 1 && subjects != 2)  {
            throw new InvalidDatabaseEntryException("Students can only take either 1 or 2 subjects");
        }
        if (subjects == null)   {
            throw new InvalidDatabaseEntryException("Subjects is null. Contact developer for help");
        }
        Integer studentId = dao.insertStudent(trimmedFullName, trimmedPrefName, subjects, Instant.now());
        studentsObserver.triggerInsert(studentId);
    }

    public void deleteStudent(Integer studentId) throws IllegalDatabaseOperationException {
        Boolean deleted;
        try {
            deleted = this.dao.deleteStudent(studentId);
        } catch (UnableToExecuteStatementException e) {
            // If unable to delete due to foreign key constraints
            // TODO Find a better way to check for errors
            if (e.getMessage().contains("FOREIGN KEY constraint")) {
                throw new IllegalDatabaseOperationException("Cannot delete student with existing visits", e);
            }
            throw new RuntimeException("Failed to delete student", e);
        }
        if (deleted) {
            // TODO when the DAO returns false, what exactly does that mean? Find out and rewrite this
            studentsObserver.triggerDelete(studentId);
        }
    }

    public void updateStudent(String fullLegalName, String prefName, Integer subjects)    {
        
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
     * Notifies subscribers of database changes
     */
    public class Observer<K, V> {
    
        private List<Consumer<V>> updateSubs;
        private List<Consumer<K>> deleteSubs;
        private List<Consumer<K>> insertSubs;

        private Observer()   {
            updateSubs = new LinkedList<Consumer<V>>();
            deleteSubs = new LinkedList<Consumer<K>>();
            insertSubs = new LinkedList<Consumer<K>>();
        }

        public void subscribeToUpdates(Consumer<V> function) {
            updateSubs.add(function);
        }

        public void subscribeToDeletes(Consumer<K> function) {
            deleteSubs.add(function);
        }

        public void subscribeToInserts(Consumer<K> function) {
            insertSubs.add(function);
        }

        private void triggerUpdate(V data) {
            updateSubs.forEach(function -> function.accept(data));
        }

        private void triggerDelete(K dataId) {
            deleteSubs.forEach(function -> function.accept(dataId));
        }

        private void triggerInsert(K dataId) {
            insertSubs.forEach(function -> function.accept(dataId));
        }

    }

    static public class InvalidDatabaseEntryException extends Exception {
        public InvalidDatabaseEntryException()  {super();}
        public InvalidDatabaseEntryException(String errorMsg)  {super(errorMsg);}
    }

    static public class IllegalDatabaseOperationException extends Exception{
        public IllegalDatabaseOperationException() {super();}
        public IllegalDatabaseOperationException(String errorMsg) {super(errorMsg);}
        public IllegalDatabaseOperationException(String errorMsg, Exception e) {super(errorMsg, e);}
    }

}
