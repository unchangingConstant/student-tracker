package io.github.unchangingconstant.studenttracker.app.gui.models;

import java.util.Collection;
import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.backend.entities.OngoingVisit;
import io.github.unchangingconstant.studenttracker.app.backend.entities.Visit;
import io.github.unchangingconstant.studenttracker.app.backend.services.VisitEventService;
import io.github.unchangingconstant.studenttracker.app.backend.services.VisitService;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

@Singleton
public class OngoingVisitTableModel {

    private SimpleListProperty<OngoingVisit> ongoingVisits;

    private VisitService visitService;

    @Inject
    public OngoingVisitTableModel(VisitService visitService, VisitEventService eventService) {
        this.visitService = visitService;
        Collection<OngoingVisit> initialData = visitService.getOngoingVisits().values();
        ongoingVisits = new SimpleListProperty<>(FXCollections.observableArrayList(initialData));
        eventService.subscribeToDeletes(visit -> onVisitDelete(visit));
        eventService.subscribeToInserts(visit -> onVisitInsert(visit));
        eventService.subscribeToUpdates(visit -> onVisitUpdate(visit));
    }

    public ObservableList<OngoingVisit> ongoingVisits() {
        return FXCollections.unmodifiableObservableList(ongoingVisits);
    }

    // This is okay since visits are immutable
    public OngoingVisit get(Integer visitId) {
        for (OngoingVisit visit : ongoingVisits) {
            if (visit.getStudentId().equals(visitId)) {
                return visit;
            }
        }
        throw new NoSuchElementException("Visit couldn't be found");
    }

    public void addListener(ListChangeListener<OngoingVisit> listener) {
        ongoingVisits.addListener(listener);
    }

    public void bind(Property<ObservableList<OngoingVisit>> prop) {
        prop.bind(ongoingVisits);
    }

    // TODO Fix this at some point. No way of telling if there are more than one
    // visits with the same id or if a visit with that id doesn't exist
    private void onVisitDelete(Integer deleted) {
        ongoingVisits.removeIf(ongoingVisit -> ongoingVisit.getStudentId().equals(deleted));
    }

    private void onVisitInsert(Integer inserted) {
        // OngoingVisit newVisit = visitService.getVisit(inserted);
        // if (newVisit.getEndTime() == null) {
        //     ongoingVisits.add(newVisit);
        // }
    }

    // TODO refactor this whole class bruh
    private void onVisitUpdate(OngoingVisit updated) {
        // Boolean removed = ongoingVisits.removeIf(visit -> visit.getStudentId().equals(updated.getStudentId()));
        // if (removed || updated.getEndTime() == null) {
        //     ongoingVisits.add(updated);
        // }
    }

}
