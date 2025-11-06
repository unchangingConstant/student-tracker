package io.github.unchangingconstant.studenttracker.app.backend.services;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.backend.dao.DatabaseDAO;
import io.github.unchangingconstant.studenttracker.app.backend.entities.OngoingVisit;
import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.backend.entities.Visit;
import lombok.Getter;

@Singleton
public class AttendanceService {
    
    private DatabaseDAO dao;

    @Getter
    private Observer<Integer, OngoingVisit> ongoingVisitsObserver;
    @Getter
    private Observer<Integer, Visit> visitsObserver;
    @Getter
    private Observer<Integer, Student> studentsObserver;

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

    public Map<Integer, OngoingVisit> getOngoingVisits() {
        return this.dao.getOngoingVisits();
    }

    public OngoingVisit getOngoingVisit(Integer studentId)   {
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
    public void endOngoingVisit(OngoingVisit ongoingVisit) {
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

    public Student getStudent(Integer studentId) {
        return this.dao.getStudent(studentId);
    }

    public Map<Integer, Student> getAllStudents() {
        return this.dao.getAllStudents();
    }

    public void insertStudent(String firstName, String middleName, String lastName, Integer subjects) throws InvalidDatabaseEntryException {
        if (!noGaps(firstName) || (middleName != null ? !noGaps(middleName) : false) || !noGaps(lastName))   {
            throw new InvalidDatabaseEntryException("Names cannot have gaps");
        }
        if (!isAlpha(firstName) || (middleName != null ? !isAlpha(middleName) : false) || !isAlpha(lastName)) {
            throw new InvalidDatabaseEntryException("Names must only contain alphabetic characters");
        }
        if (subjects > 2 || subjects < 0)   {
            throw new InvalidDatabaseEntryException("A student may only take 2 subjects at a time");
        }
        Integer inserted = this.dao.insertStudent(firstName, middleName, lastName, subjects, Instant.now());
        studentsObserver.triggerInsert(inserted);
    }

    public void deleteStudent(Integer studentId) {
        Boolean deleted;
        try {
            deleted = this.dao.deleteStudent(studentId);
        } catch (UnableToExecuteStatementException e) {
            // If unable to delete due to foreign key constraints
            if (e.getMessage().contains("FOREIGN KEY constraint")) {
                throw new IllegalStateException("Cannot delete student with existing visits", e);
            }
            throw new RuntimeException("Failed to delete student", e);

        }
        if (deleted) {
            studentsObserver.triggerDelete(studentId);
        }
    }

    /*
     * VISIT METHODS
     */

    public Visit getVisit(Integer visitId) {
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

    public class InvalidDatabaseEntryException extends Exception {
        public InvalidDatabaseEntryException()  {super();}
        public InvalidDatabaseEntryException(String errorMsg)  {super(errorMsg);}
    }

    /*
     * Helper methods
     */

    private boolean isAlpha(String s)   {
        return s.matches("[a-zA-Z]+");
    }

    private boolean noGaps(String s)  {
        return !s.matches(".*\\s.*");
    }

}
