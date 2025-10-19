package io.github.unchangingconstant.studenttracker.app.models;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.entities.Visit;
import io.github.unchangingconstant.studenttracker.app.services.VisitEventService;
import io.github.unchangingconstant.studenttracker.app.services.VisitService;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;

public class OngoingVisitsModel {

    private SimpleMapProperty<Integer, Visit> ongoingVisits;
    private VisitService dbAccess;

    @Inject
    public OngoingVisitsModel(VisitService dbAccess, VisitEventService eventService) {
        Map<Integer, Visit> initialData = dbAccess.getOngoingVisits();
        this.ongoingVisits = new SimpleMapProperty<Integer, Visit>(
                FXCollections.observableHashMap());
        this.ongoingVisits.putAll(initialData);
        // subscribes to database events to maintain state accuracy
        eventService.subscribeToDeletes(visitId -> onDeleteVisit(visitId));
        eventService.subscribeToInserts(visitId -> onInsertVisit(visitId));
        eventService.subscribeToUpdates(visit -> onUpdateVisit(visit));
        this.dbAccess = dbAccess;
    }

    private void onInsertVisit(Integer visitId) {
        Visit inserted = dbAccess.getVisit(visitId);
        if (inserted.getEndTime() == null) {
            ongoingVisits.put(visitId, inserted);
        }
    }

    private void onDeleteVisit(Integer visitId) {
        ongoingVisits.remove(visitId);
    }

    private void onUpdateVisit(Visit visit) {
        if (visit.getEndTime() != null) {
            ongoingVisits.remove(visit.getVisitId());
            return;
        }
        ongoingVisits.put(visit.getVisitId(), visit);
    }
}
