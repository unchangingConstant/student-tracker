package io.github.unchangingconstant.studenttracker.app.services;

import java.util.List;
import java.util.Map;

import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;
import io.github.unchangingconstant.studenttracker.app.entities.Student;

@Singleton
public class StudentService {

    private StudentEventService eventService;
    private DatabaseDAO dao;

    @Inject
    public StudentService(StudentEventService eventService, DatabaseDAO dao) {
        this.eventService = eventService;
        this.dao = dao;
    }

    public Integer insertStudent(String firstName, String middleName, String lastName, Integer subjects) {
        Integer inserted = this.dao.insertStudent(firstName, middleName, lastName, subjects);
        eventService.triggerInsert(inserted);
        return inserted;
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
            eventService.triggerDelete(studentId);
        }
    }

    public Student getStudent(Integer studentId) {
        return this.dao.getStudent(studentId);
    }

    public Map<Integer, Student> getAllStudents() {
        return this.dao.getAllStudents();
    }

}