package com.github.unchangingconstant.studenttracker.gui.utils;

import javafx.beans.binding.ListBinding;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class MapToListBinding<K, V> extends ListBinding<V> {

    public MapToListBinding(ObservableMap<K, V> map) {
        bind(map);
        addAll(map.values());
        map.addListener(new MapChangeListener<K, V>() {
            @Override
            public void onChanged(Change<? extends K, ? extends V> change) {
                if (change.wasAdded()) {
                    add(change.getValueAdded());
                } else if (change.wasRemoved()) {
                    remove(change.getValueRemoved());
                }
            }
        });
    }

    @Override
    protected ObservableList<V> computeValue() {
        // This Binding tracks changes via listeners, so it will always reflect the correct list
        // the alternative is recalculating the whole list upon each invalidation
        return this;
    }
}
