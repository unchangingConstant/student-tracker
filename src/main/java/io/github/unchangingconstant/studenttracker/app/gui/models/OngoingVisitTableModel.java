package io.github.unchangingconstant.studenttracker.app.gui.models;

import java.util.Collection;
import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.backend.entities.OngoingVisit;
import io.github.unchangingconstant.studenttracker.app.backend.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.backend.services.AttendanceService.Observer;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

@Singleton
public class OngoingVisitTableModel {

    private SimpleListProperty<OngoingVisit> ongoingVisits;
    private AttendanceService attendanceService;

    @Inject
    public OngoingVisitTableModel(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
        Collection<OngoingVisit> initialData = attendanceService.getOngoingVisits().values();
        ongoingVisits = new SimpleListProperty<>(FXCollections.observableArrayList(initialData));
        Observer<Integer, OngoingVisit> observer = attendanceService.getOngoingVisitsObserver();
        observer.subscribeToDeletes(visit -> onOngoingVisitDelete(visit));
        observer.subscribeToInserts(visit -> onOngoingVisitInsert(visit));
        observer.subscribeToUpdates(visit -> onOngoingVisitUpdate(visit));
    }

    public ObservableList<OngoingVisit> ongoingVisits() {
        return FXCollections.unmodifiableObservableList(ongoingVisits);
    }

    // This is okay since visits are immutable
    public OngoingVisit get(Integer studentId) {
        for (OngoingVisit visit : ongoingVisits) {
            if (visit.getStudentId().equals(studentId)) {
                return visit;
            }
        }
        throw new NoSuchElementException(String.format("Visit with studentId %d could not be found", studentId));
    }

    public void addListener(ListChangeListener<OngoingVisit> listener) {
        ongoingVisits.addListener(listener);
    }

    public void bind(Property<ObservableList<OngoingVisit>> prop) {
        prop.bind(ongoingVisits);
    }

    private void onOngoingVisitDelete(Integer deleted) {
        ongoingVisits.removeIf(ongoingVisit -> ongoingVisit.getStudentId().equals(deleted));
    }

    private void onOngoingVisitInsert(Integer inserted) {
        OngoingVisit newVisit = attendanceService.getOngoingVisit(inserted);
        ongoingVisits.add(newVisit);
    }

    private void onOngoingVisitUpdate(OngoingVisit updated) {
        for (int i = 0; i < ongoingVisits.size(); i++){
            if (ongoingVisits.get(i).getStudentId().equals(updated.getStudentId())) {
                ongoingVisits.set(i, updated);
                return;
            }
        }
        throw new NoSuchElementException(String.format("OngoingVisit with studentId %d not found", updated.getStudentId()));
    }

}
