package io.github.unchangingconstant.studenttracker.app.gui.viewmodels.bindings;

import javafx.beans.binding.MapBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ListChangeListener.Change;

public abstract class ListToMapBinding<T, K, V> extends MapBinding<K, V> {

    private ObservableMap<K, V> currMap;

    public ListToMapBinding(ObservableList<T> list) {
        super.bind(list);
        list.addListener(new ListChangeListener<T>() {
            @Override
            public void onChanged(Change<? extends T> evt) {
                onListChange(evt);
            }
        });
        currMap = FXCollections.emptyObservableMap();
    }

    private void onListChange(Change<? extends T> evt) {
        if (evt.wasAdded()) {
            evt.getAddedSubList().forEach(added -> {
                currMap.put(keyFactory(added), valueFactory(added));
            });
        } else if (evt.wasRemoved()) {
            evt.getRemoved().forEach(removed -> {
                currMap.remove(keyFactory(removed));
            });
        }
    }

    protected abstract K keyFactory(T item);

    protected abstract V valueFactory(T item);

    @Override
    protected ObservableMap<K, V> computeValue() {
        return currMap;
    }

}
