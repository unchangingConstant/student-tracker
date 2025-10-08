package io.github.unchangingconstant.studenttracker.ui.models;

import java.util.List;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.services.VisitsTableEventService;
import io.github.unchangingconstant.studenttracker.app.services.VisitsTableService;
import io.github.unchangingconstant.studenttracker.entities.Student;
import io.github.unchangingconstant.studenttracker.entities.Visit;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class OngoingVisitsModel {

    private SimpleListProperty<Visit> ongoingVisits;
    private VisitsTableService dbAccess;

    @Inject
    public OngoingVisitsModel(VisitsTableService dbAccess, VisitsTableEventService eventService) {
        List<Visit> initialData = dbAccess.getOngoingVisits();
        this.ongoingVisits = new SimpleListProperty<Visit>(FXCollections.observableArrayList(initialData));
        // subscribes to database events to maintain state accuracy
        eventService.subscribeToDeletes(visitId -> onDeleteVisit(visitId));
        eventService.subscribeToInserts(visitId -> onInsertVisit(visitId));

        this.dbAccess = dbAccess;
    }

    private void onInsertVisit(Integer visitId) {
        ongoingVisits.add(dbAccess.getVisit(visitId));
    }

    private void onDeleteVisit(Integer visitId) {
        for (Visit visit : ongoingVisits) {
            if (visit.getVisitId().equals(visitId)) {
                ongoingVisits.remove(visit);
                break;
            }
        }
    }
}
