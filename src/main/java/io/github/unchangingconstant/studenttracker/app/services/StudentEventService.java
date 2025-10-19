package io.github.unchangingconstant.studenttracker.app.services;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.entities.domain.Student;

@Singleton
public class StudentEventService {

    private List<Consumer<Student>> updateSubs;
    private List<Consumer<Integer>> deleteSubs;
    private List<Consumer<Integer>> insertSubs;

    @Inject
    public StudentEventService() {
        updateSubs = new LinkedList<Consumer<Student>>();
        deleteSubs = new LinkedList<Consumer<Integer>>();
        insertSubs = new LinkedList<Consumer<Integer>>();
    }

    public void subscribeToUpdates(Consumer<Student> function) {
        updateSubs.add(function);
    }

    public void subscribeToDeletes(Consumer<Integer> function) {
        deleteSubs.add(function);
    }

    public void subscribeToInserts(Consumer<Integer> function) {
        insertSubs.add(function);
    }

    protected void triggerUpdate(Student student) {
        updateSubs.forEach(function -> function.accept(student));
    }

    protected void triggerDelete(Integer studentId) {
        deleteSubs.forEach(function -> function.accept(studentId));
    }

    protected void triggerInsert(Integer studentId) {
        insertSubs.forEach(function -> function.accept(studentId));
    }

}
