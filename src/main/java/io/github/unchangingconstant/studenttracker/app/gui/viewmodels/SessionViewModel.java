package io.github.unchangingconstant.studenttracker.app.gui.viewmodels;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.backend.entities.Visit;
import io.github.unchangingconstant.studenttracker.app.backend.services.VisitService;
import io.github.unchangingconstant.studenttracker.app.gui.models.OngoingVisitTableModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import lombok.Getter;

public class SessionViewModel {

    private OngoingVisitTableModel model;
    // Corresponds update-second to VisitIds for efficient updating
    @Getter
    private SimpleMapProperty<Long, List<Integer>> updateQueue;
    // Corresponds visitId to timeRemaining
    @Getter
    private SimpleMapProperty<Integer, SimpleLongProperty> timeRemainingRef;
    private Timeline clock;

    @Inject
    public SessionViewModel(OngoingVisitTableModel model, VisitService service) {
        this.model = model;
        setupTimeRemainingRef();
        setupUpdateQueue();
        setupClock();
    }

    public void bindToTimeRemainingProperty(SimpleLongProperty prop, Integer visitId) {
        prop.bind(timeRemainingRef.get(visitId));
    }

    public void bindToModelProperty(Property<ObservableList<Visit>> prop) {
        model.bind(prop);
    }

    private void updateRemainingTime() {
        Instant now = Instant.now();
        updateQueue.get(now.getEpochSecond() % 60).forEach(visitId -> timeRemainingRef.get(visitId)
                .set(ChronoUnit.MINUTES.between(model.get(visitId).getStartTime(), now)));

    }

    private void setupClock() {
        clock = new Timeline(new KeyFrame(
                Duration.seconds(1),
                event -> {
                    updateRemainingTime();
                }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void setupTimeRemainingRef() {
        timeRemainingRef = new SimpleMapProperty<>(FXCollections.observableHashMap());
        // TODO POPULATE THE LIST A DIFFERENT WAY OH MY GOD!!!!!!
        model.readOnlyList().forEach(visit -> timeRemainingRef.put(visit.getVisitId(),
                new SimpleLongProperty(ChronoUnit.MINUTES.between(visit.getStartTime(), Instant.now()))));
        model.addListener(new ListChangeListener<Visit>() {
            @Override
            public void onChanged(Change<? extends Visit> evt) {
                if (evt.wasAdded()) {
                    evt.getAddedSubList().forEach(visit -> timeRemainingRef.put(visit.getVisitId(),
                            new SimpleLongProperty(ChronoUnit.MINUTES.between(Instant.now(), visit.getStartTime()))));
                } else if (evt.wasRemoved()) {
                    evt.getRemoved().forEach(visit -> timeRemainingRef.remove(visit.getVisitId()));
                }
            }
        });
    }

    private void setupUpdateQueue() {
        updateQueue = new SimpleMapProperty<>(FXCollections.observableHashMap());
        for (Long i = (long) 0; i < 60; i++) {
            updateQueue.put(i, new LinkedList<Integer>());
        }
        // TODO POPULATE THE LIST A DIFFERENT WAY OH MY GOD!!!!!!
        model.readOnlyList()
                .forEach(visit -> updateQueue.get(visit.getStartTime().getEpochSecond() % 60).add(visit.getVisitId()));

        model.addListener(new ListChangeListener<Visit>() {
            @Override
            public void onChanged(Change<? extends Visit> evt) {
                if (evt.wasAdded()) {
                    evt.getAddedSubList().forEach(visit -> updateQueue.get(visit.getStartTime().getEpochSecond() % 60)
                            .add(visit.getVisitId()));
                } else if (evt.wasRemoved()) {
                    evt.getRemoved().forEach(visit -> updateQueue.get(visit.getStartTime().getEpochSecond() % 60)
                            .removeIf(visitId -> visitId.equals(visit.getVisitId())));
                }
            }
        });
    }
}
