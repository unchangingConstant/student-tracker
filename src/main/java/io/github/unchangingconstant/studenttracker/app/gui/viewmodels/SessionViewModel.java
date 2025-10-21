package io.github.unchangingconstant.studenttracker.app.gui.viewmodels;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Visit;
import io.github.unchangingconstant.studenttracker.app.gui.models.OngoingVisitTableModel;
import io.github.unchangingconstant.studenttracker.app.gui.viewmodels.bindings.TimeRemainingToOngoingVisitsBinding;
import io.github.unchangingconstant.studenttracker.app.gui.viewmodels.bindings.UpdateQueueToOngoingVisitsBinding;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.MapBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class SessionViewModel {

    private OngoingVisitTableModel model;
    private ObjectProperty<ObservableList<Visit>> modelProp;
    private UpdateQueueToOngoingVisitsBinding updateQueue;
    private TimeRemainingToOngoingVisitsBinding timeRemainingRef;

    public SessionViewModel(OngoingVisitTableModel model) {
        this.model = model;
        model.bind(modelProp);
        this.timeRemainingRef = new TimeRemainingToOngoingVisitsBinding(modelProp.get());
        this.updateQueue = new UpdateQueueToOngoingVisitsBinding(modelProp.get());
    }

    public void bindToTimeRemainingProperty(SimpleLongProperty prop, Integer visitId) {
        prop.bind(timeRemainingRef.get(visitId));
    }

    private void updateRemainingTime() {
        LocalDateTime now = LocalDateTime.now();
        updateQueue.get(now.getMinute()).forEach(visitId -> {
            timeRemainingRef.get(visitId).set(ChronoUnit.MINUTES.between(now, modelProp.get().get(0).getStartTime()));
        });

    }

}
