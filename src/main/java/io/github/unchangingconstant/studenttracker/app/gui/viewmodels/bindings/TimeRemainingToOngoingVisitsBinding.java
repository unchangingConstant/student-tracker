package io.github.unchangingconstant.studenttracker.app.gui.viewmodels.bindings;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Visit;
import javafx.beans.binding.MapBinding;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class OngoingVisitsToTimeRemainingBinding extends MapBinding<Integer, SimpleLongProperty> {

    private ObservableMap<Integer, SimpleLongProperty> currMap;

    public OngoingVisitsToTimeRemainingBinding(ObservableList<Visit> ongoingVisits) {
        // Populate currMap with initial data
        this.currMap = FXCollections.emptyObservableMap();
        ongoingVisits.forEach(visit -> {
            this.currMap.put(visit.getVisitId(),
                    new SimpleLongProperty(ChronoUnit.MINUTES.between(visit.getStartTime(), LocalDateTime.now())));
        });
        // Subscribe to all change events
        ongoingVisits.addListener(new ListChangeListener<Visit>() {
            @Override
            public void onChanged(Change<? extends Visit> evt) {
                onListChange(evt);
            }
        });
    }

    private void onListChange(ListChangeListener.Change<? extends Visit> evt) {
        if (evt.wasAdded()) {
            onAdded(evt.getAddedSubList());
        } else if (evt.wasRemoved()) {
            onRemoved(evt.getRemoved());
        }
    }

    private void onAdded(List<? extends Visit> added) {
        added.forEach(visit -> {
            currMap.put(visit.getVisitId(),
                    new SimpleLongProperty(ChronoUnit.MINUTES.between(visit.getStartTime(), LocalDateTime.now())));
        });
    }

    private void onRemoved(List<? extends Visit> removed) {
        removed.forEach(visit -> {
            currMap.remove(visit.getVisitId());
        });
    }

    @Override
    protected ObservableMap<Integer, SimpleLongProperty> computeValue() {
        // currmap always reflects actual state
        return currMap;
    }

}
