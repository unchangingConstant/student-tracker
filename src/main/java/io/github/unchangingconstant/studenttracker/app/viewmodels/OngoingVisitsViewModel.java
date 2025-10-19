package io.github.unchangingconstant.studenttracker.app.viewmodels;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.entities.Visit;
import io.github.unchangingconstant.studenttracker.app.models.OngoingVisitsModel;
import io.github.unchangingconstant.studenttracker.app.services.VisitService;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import lombok.Getter;

public class OngoingVisitsViewModel {

    @Getter
    private SimpleMapProperty<Integer, Visit> ongoingVisits;
    @Getter
    private SimpleMapProperty<Integer, SimpleLongProperty> timeRemaining;

    private VisitService visitService;

    @Inject
    public OngoingVisitsViewModel(VisitService visitService, OngoingVisitsModel ongoingVisitsModel) {
        this.visitService = visitService;
        // Every one minutes, updates the time remaining of each scheduled vist
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            timeRemaining.forEach((visitId, timeRemaining) -> timeRemaining
                    .set(30 - ChronoUnit.MINUTES.between(ongoingVisits.get(visitId).getStartTime(),
                            LocalDateTime.now())));
        }, 1, 1, TimeUnit.MINUTES);

        ongoingVisits = new SimpleMapProperty<>(FXCollections.emptyObservableMap());
        ongoingVisitsModel.bind(ongoingVisits);
        System.out.println(
                "-----------------xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx---------------------------*--------------------");
        System.out.println(ongoingVisits.size());

        timeRemaining = new SimpleMapProperty<>(FXCollections.emptyObservableMap());
        ongoingVisits.forEach((visitId, visit) -> {
            SimpleLongProperty initTimeRemaining = new SimpleLongProperty(30 -
                    ChronoUnit.MINUTES.between(visit.getStartTime(), LocalDateTime.now()));
            timeRemaining.put(visitId, initTimeRemaining);
        });
    }

    public void onEndVisitPress(Integer visitId) {
        visitService.endVisit(visitId);
    }

}
