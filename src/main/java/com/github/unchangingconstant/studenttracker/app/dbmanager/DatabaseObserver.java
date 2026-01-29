package com.github.unchangingconstant.studenttracker.app.dbmanager;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Notifies subscribers of triggers events
 */
public class DatabaseObserver<T> {

    private final List<Consumer<List<T>>> updateSubs;
    private final List<Consumer<List<T>>> deleteSubs;
    private final List<Consumer<List<T>>> insertSubs;

    protected DatabaseObserver()   {
        updateSubs = new LinkedList<Consumer<List<T>>>();
        deleteSubs = new LinkedList<Consumer<List<T>>>();
        insertSubs = new LinkedList<Consumer<List<T>>>();
    }

    public void subscribeToUpdates(Consumer<List<T>> function) {
        updateSubs.add(function);
    }

    public void subscribeToDeletes(Consumer<List<T>> function) {
        deleteSubs.add(function);
    }

    public void subscribeToInserts(Consumer<List<T>> function) {
        insertSubs.add(function);
    }

    protected void triggerUpdate(List<T> data) {
        notifySubs(updateSubs, data);
    }

    protected void triggerUpdate(T data) {
        triggerUpdate(toList(data));
    }

    protected void triggerDelete(List<T> data) {
        notifySubs(deleteSubs, data);
    }

    protected void triggerDelete(T data) {
        triggerDelete(toList(data));
    }

    protected void triggerInsert(List<T> data) {
        notifySubs(insertSubs, data);
    }

    protected void triggerInsert(T data) {
        triggerInsert(toList(data));
    }

    private List<T> toList(T data) {
        List<T> list = new LinkedList<>();
        list.add(data);
        return list;
    }

    private <D> void notifySubs(List<Consumer<D>> subs, D data) {
        subs.forEach(function -> {
            try {
                function.accept(data);
            } catch (Exception e) {
                // Exception upon notifying this sub
                e.printStackTrace();
            }
        });
    }

}