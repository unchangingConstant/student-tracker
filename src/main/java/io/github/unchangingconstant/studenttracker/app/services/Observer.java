package io.github.unchangingconstant.studenttracker.app.services;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Notifies subscribers of triggers events
 */
public class Observer<K, V> {

    private List<Consumer<V>> updateSubs;
    private List<Consumer<K>> deleteSubs;
    private List<Consumer<K>> insertSubs;

    protected Observer()   {
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

    protected void triggerUpdate(V data) {
        updateSubs.forEach(function -> function.accept(data));
    }

    protected void triggerDelete(K dataId) {
        deleteSubs.forEach(function -> function.accept(dataId));
    }

    protected void triggerInsert(K dataId) {
        insertSubs.forEach(function -> function.accept(dataId));
    }

}