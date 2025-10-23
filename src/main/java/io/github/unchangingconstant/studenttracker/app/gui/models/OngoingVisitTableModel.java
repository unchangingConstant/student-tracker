package io.github.unchangingconstant.studenttracker.app.gui.models;

import java.util.Collection;
import java.util.Iterator;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Visit;
import io.github.unchangingconstant.studenttracker.app.backend.services.VisitEventService;
import io.github.unchangingconstant.studenttracker.app.backend.services.VisitService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

@Singleton
public class OngoingVisitTableModel {

    private SimpleListProperty<Visit> ongoingVisits;

    private VisitService visitService;

    @Inject
    public OngoingVisitTableModel(VisitService visitService, VisitEventService eventService) {
        this.visitService = visitService;
        Collection<Visit> initialData = visitService.getOngoingVisits().values();
        ongoingVisits = new SimpleListProperty<>(FXCollections.observableArrayList(initialData));
        eventService.subscribeToDeletes(visit -> onVisitDelete(visit));
        eventService.subscribeToInserts(visit -> onVisitInsert(visit));
        eventService.subscribeToUpdates(visit -> onVisitUpdate(visit));
    }

    public Iterable<Visit> iterator() {
        return ongoingVisits.iterator();
    }

    // This is okay since visits are immutable
    public Visit get(Integer visitId) {
        return ongoingVisits.get(visitId);
    }

    public void addListener(ListChangeListener<Visit> listener) {
        ongoingVisits.addListener(listener);
    }

    public void bind(Property<ObservableList<Visit>> prop) {
        prop.bind(ongoingVisits);
    }

    // TODO Fix this at some point. No way of telling if there are more than one
    // visits with the same id or if a visit with that id doesn't exist
    private void onVisitDelete(Integer deleted) {
        Boolean result = ongoingVisits.removeIf(ongoingVisit -> ongoingVisit.getVisitId().equals(deleted));
    }

    private void onVisitInsert(Integer inserted) {
        Visit newVisit = visitService.getVisit(inserted);
        if (newVisit.getEndTime() == null) {
            ongoingVisits.add(newVisit);
        }
    }

    private void onVisitUpdate(Visit updated) {
        Boolean removed = ongoingVisits.removeIf(visit -> visit.getVisitId().equals(updated.getVisitId()));
        if (removed || updated.getEndTime() == null) {
            ongoingVisits.add(updated);
        }
    }

}
