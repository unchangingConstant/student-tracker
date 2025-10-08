package io.github.unchangingconstant.studenttracker.app.services;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;
import io.github.unchangingconstant.studenttracker.app.entities.Student;

// TODO examine data validation more rigorously
// TODO consider how error handling should be implemented
@Singleton
public class DatabaseAccessService {

    private DatabaseEventService eventService;
    private DatabaseDAO dao;

    @Inject
    public DatabaseAccessService(DatabaseEventService eventService, DatabaseDAO dao) {
        this.eventService = eventService;
        this.dao = dao;
    }

    public Integer insertStudent(String firstName, String middleName, String lastName, short subjects) {
        Integer result = this.dao.insertStudent(firstName, middleName, lastName, subjects);
        eventService.triggerInsert(result);
        return result;
    }

    public void deleteStudent(Integer studentId) {
        this.dao.deleteStudent(studentId);
        eventService.triggerDelete(studentId);
    }

    public Student getStudent(Integer studentId) {
        return this.dao.getStudent(studentId);
    }

    public List<Student> getAllStudents() {
        return this.dao.getAllStudents();
    }

}