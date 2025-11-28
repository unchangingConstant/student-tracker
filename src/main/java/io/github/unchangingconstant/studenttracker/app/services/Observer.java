package io.github.unchangingconstant.studenttracker.app.services;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Notifies subscribers of triggers events
 */
public class Observer<T> {

    private List<Consumer<T>> updateSubs;
    private List<Consumer<T>> deleteSubs;
    private List<Consumer<T>> insertSubs;

    protected Observer()   {
        updateSubs = new LinkedList<Consumer<T>>();
        deleteSubs = new LinkedList<Consumer<T>>();
        insertSubs = new LinkedList<Consumer<T>>();
    }

    public void subscribeToUpdates(Consumer<T> function) {
        updateSubs.add(function);
    }

    public void subscribeToDeletes(Consumer<T> function) {
        deleteSubs.add(function);
    }

    public void subscribeToInserts(Consumer<T> function) {
        insertSubs.add(function);
    }

    protected void triggerUpdate(T data) {
        updateSubs.forEach(function -> function.accept(data));
    }

    protected void triggerDelete(T dataId) {
        deleteSubs.forEach(function -> function.accept(dataId));
    }

    protected void triggerInsert(T dataId) {
        insertSubs.forEach(function -> function.accept(dataId));
    }

}