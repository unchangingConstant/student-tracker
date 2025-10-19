package io.github.unchangingconstant.studenttracker.app.services;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.entities.Visit;

@Singleton
public class VisitEventService {
    private List<Consumer<Visit>> updateSubs;
    private List<Consumer<Integer>> deleteSubs;
    private List<Consumer<Integer>> insertSubs;

    @Inject
    public VisitEventService() {
        updateSubs = new LinkedList<Consumer<Visit>>();
        deleteSubs = new LinkedList<Consumer<Integer>>();
        insertSubs = new LinkedList<Consumer<Integer>>();
    }

    public void subscribeToUpdates(Consumer<Visit> function) {
        updateSubs.add(function);
    }

    public void subscribeToDeletes(Consumer<Integer> function) {
        deleteSubs.add(function);
    }

    public void subscribeToInserts(Consumer<Integer> function) {
        insertSubs.add(function);
    }

    protected void triggerUpdate(Visit visit) {
        updateSubs.forEach(function -> function.accept(visit));
    }

    protected void triggerDelete(Integer visitId) {
        deleteSubs.forEach(function -> function.accept(visitId));
    }

    protected void triggerInsert(Integer visitId) {
        insertSubs.forEach(function -> function.accept(visitId));
    }
}
