package io.github.unchangingconstant.studenttracker.app.gui.viewmodels;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.OngoingVisit;
import io.github.unchangingconstant.studenttracker.app.backend.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.gui.models.OngoingVisitTableModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import lombok.Getter;

public class SessionViewModel {

    private OngoingVisitTableModel model;
    // Corresponds update-second to ongoing visits for efficient updating
    @Getter
    private SimpleMapProperty<Long, List<Integer>> updateQueue;
    // Corresponds visitId to timeRemaining
    @Getter
    private SimpleMapProperty<Integer, SimpleLongProperty> timeRemainingRef;
    private Timeline clock;

    @Inject
    public SessionViewModel(OngoingVisitTableModel model, AttendanceService service) {
        this.model = model;
        setupTimeRemainingRef();
        setupUpdateQueue();
        setupClock();
    }

    public void bindToTimeRemainingProperty(SimpleLongProperty prop, Integer studentId) {
        prop.bind(timeRemainingRef.get(studentId));
    }

    public void bindToModelProperty(Property<ObservableList<OngoingVisit>> prop) {
        model.bind(prop);
    }

    private void updateRemainingTime() {
        Instant now = Instant.now();
        updateQueue.get(now.getEpochSecond() % 60).forEach(studentId -> timeRemainingRef.get(studentId)
                .set(ChronoUnit.MINUTES.between(model.get(studentId).getStartTime(), now)));
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
        model.ongoingVisits().forEach(ongoingVisit -> timeRemainingRef.put(ongoingVisit.getStudentId(),
                new SimpleLongProperty(ChronoUnit.MINUTES.between(ongoingVisit.getStartTime(), Instant.now()))));
        model.addListener(new ListChangeListener<OngoingVisit>() {
            @Override
            public void onChanged(Change<? extends OngoingVisit> evt) {
                if (evt.wasAdded()) {
                    evt.getAddedSubList().forEach(visit -> timeRemainingRef.put(visit.getStudentId(),
                            new SimpleLongProperty(ChronoUnit.MINUTES.between(Instant.now(), visit.getStartTime()))));
                } else if (evt.wasRemoved()) {
                    evt.getRemoved().forEach(visit -> timeRemainingRef.remove(visit.getStudentId()));
                }
            }
        });
    }

    private void setupUpdateQueue() {
        updateQueue = new SimpleMapProperty<>(FXCollections.observableHashMap());
        for (Long i = (long) 0; i < 60; i++) {
            updateQueue.put(i, new LinkedList<Integer>());
        }
        model.ongoingVisits()
                .forEach(visit -> updateQueue.get(visit.getStartTime().getEpochSecond() % 60).add(visit.getStudentId()));
        model.addListener(new ListChangeListener<OngoingVisit>() {
            @Override
            public void onChanged(Change<? extends OngoingVisit> evt) {
                if (evt.wasAdded()) {
                    evt.getAddedSubList().forEach(visit -> updateQueue.get(visit.getStartTime().getEpochSecond() % 60)
                            .add(visit.getStudentId()));
                } else if (evt.wasRemoved()) {
                    evt.getRemoved().forEach(visit -> updateQueue.get(visit.getStartTime().getEpochSecond() % 60)
                            .removeIf(visitId -> visitId.equals(visit.getStudentId())));
                }
            }
        });
    }
}
