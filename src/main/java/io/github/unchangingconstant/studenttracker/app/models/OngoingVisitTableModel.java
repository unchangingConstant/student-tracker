package io.github.unchangingconstant.studenttracker.app.models;

import java.util.Collection;

import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.entities.Visit;
import io.github.unchangingconstant.studenttracker.app.services.VisitEventService;
import io.github.unchangingconstant.studenttracker.app.services.VisitService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Singleton
public class OngoingVisitTableModel {

    private SimpleListProperty<OngoingVisitModel> ongoingVisits;

    private VisitService visitService;

    public OngoingVisitTableModel(VisitService visitService, VisitEventService eventService) {
        this.visitService = visitService;
        Collection<Visit> initialData = visitService.getOngoingVisits().values();
        ongoingVisits = new SimpleListProperty<>(FXCollections.observableArrayList());

        initialData.forEach(visit -> {

            SimpleLongProperty timeRemaining = new SimpleLongProperty();

            ongoingVisits.add(null);
        });

        eventService.subscribeToDeletes(visit -> onVisitDelete(visit));
        eventService.subscribeToInserts(visit -> onVisitInsert(visit));
        eventService.subscribeToUpdates(visit -> onVisitUpdate(visit));
    }

    public void bind(ObjectProperty<ObservableList<OngoingVisitModel>> prop) {
        prop.bind(ongoingVisits);
    }

    // TODO Fix this at some point. No way of telling if there are more than one
    // visits with the same id or if a visit with that id doesn't exist
    private void onVisitDelete(Integer deleted) {
        Boolean result = ongoingVisits.removeIf(ongoingVisit -> ongoingVisit.getVisitId().equals(deleted));
        if (!result) {
            throw new RuntimeException("Illegal mismatch between model and database states");
        }
    }

    private void onVisitInsert(Integer inserted) {

    }

    private void onVisitUpdate(Visit updated) {

    }

}
