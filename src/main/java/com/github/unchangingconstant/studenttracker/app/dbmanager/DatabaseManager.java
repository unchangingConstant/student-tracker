package com.github.unchangingconstant.studenttracker.app.dbmanager;

import java.util.*;

import com.github.unchangingconstant.studenttracker.app.entities.OngoingVisit;
import com.github.unchangingconstant.studenttracker.app.entities.Student;
import com.github.unchangingconstant.studenttracker.app.entities.Visit;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import lombok.Getter;

@Singleton
public class DatabaseManager {

    private final AttendanceDAO dao;

    @Getter
    private final DatabaseObserver<OngoingVisit> ongoingVisitsObserver;
    @Getter
    private final DatabaseObserver<Visit> visitsObserver;
    @Getter
    private final DatabaseObserver<Student> studentsObserver;

    @Inject
    public DatabaseManager(AttendanceDAO dao)  {
        this.dao = dao;
        this.ongoingVisitsObserver = new DatabaseObserver<>();
        this.visitsObserver = new DatabaseObserver<>();
        this.studentsObserver = new DatabaseObserver<>();
    }

    /*
     * STUDENT METHODS
     */

    public Optional<Student> findStudent(Integer studentId) {
        return dao.findStudent(studentId);
    }

    public Map<Integer, Student> getAllStudents() {
        return dao.getAllStudents();
    }

    public void insertStudent(Student student) throws InvalidEntityException {
        if (!Student.validate(student)) {
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
        if (!Student.validate(student) || student.getStudentId() == null) {
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
        if (!Visit.validate(visit)) {
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

    public Map<Integer, OngoingVisit> getOngoingVisits() {
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
        Visit endedVisit = Visit.builder()
            .studentId(ongoingVisit.getStudentId())
            .startTime(ongoingVisit.getStartTime())
            .duration(duration).build();
        dao.endOngoingVisit(endedVisit);
        ongoingVisitsObserver.triggerDelete(ongoingVisit);
        visitsObserver.triggerInsert(endedVisit);
    }

    // TODO Make this a Domain object exception, not a service exception. See issue #16 or refer to the pragmatic programmer on DRY
    static public class InvalidEntityException extends Exception {
        public InvalidEntityException()  {super();}
    }

    static public class NoSuchEntityException extends NoSuchElementException {
        public NoSuchEntityException(Integer id) {
            super("Entity with unique identifier " + String.valueOf(id) + " could not be found.");
        }
    }

}
