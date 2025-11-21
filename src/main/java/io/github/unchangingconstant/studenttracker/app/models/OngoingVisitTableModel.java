package io.github.unchangingconstant.studenttracker.app.models;

import java.util.Collection;
import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.domain.OngoingVisitDomain;
import io.github.unchangingconstant.studenttracker.app.mappers.model.DomainToOngoingVisitModel;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.services.Observer;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

@Singleton
public class OngoingVisitTableModel {

    private SimpleListProperty<OngoingVisitModel> ongoingVisits;
    private AttendanceService attendanceService;

    @Inject
    public OngoingVisitTableModel(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
        Collection<OngoingVisitDomain> initialData = attendanceService.getOngoingVisits().values();
        ongoingVisits = new SimpleListProperty<>(FXCollections.observableArrayList());
        initialData.forEach(domain -> ongoingVisits.add(DomainToOngoingVisitModel.map(domain)));
        Observer<Integer, OngoingVisitDomain> observer = attendanceService.getOngoingVisitsObserver();
        observer.subscribeToDeletes(visit -> onOngoingVisitDelete(visit));
        observer.subscribeToInserts(visit -> onOngoingVisitInsert(visit));
        observer.subscribeToUpdates(visit -> onOngoingVisitUpdate(visit));
    }

    public ObservableList<OngoingVisitModel> ongoingVisits() {
        return FXCollections.unmodifiableObservableList(ongoingVisits);
    }

    // This is okay since visits are immutable
    public OngoingVisitModel get(Integer studentId) {
        for (OngoingVisitModel visit : ongoingVisits) {
            if (visit.getStudentId().equals(studentId)) {
                return visit;
            }
        }
        throw new NoSuchElementException(String.format("Visit with studentId %d could not be found", studentId));
    }

    public void addListener(ListChangeListener<OngoingVisitModel> listener) {
        ongoingVisits.addListener(listener);
    }

    public void bindProperty(Property<ObservableList<OngoingVisitModel>> prop) {
        prop.bind(ongoingVisits);
    }

    private void onOngoingVisitDelete(Integer deleted) {
        ongoingVisits.removeIf(ongoingVisit -> ongoingVisit.getStudentId().equals(deleted));
    }

    private void onOngoingVisitInsert(Integer inserted) {
        OngoingVisitModel newVisit = DomainToOngoingVisitModel.map(attendanceService.getOngoingVisit(inserted));
        ongoingVisits.add(newVisit);
    }

    private void onOngoingVisitUpdate(OngoingVisitDomain updated) {
        for (int i = 0; i < ongoingVisits.size(); i++){
            if (ongoingVisits.get(i).getStudentId().equals(updated.getStudentId())) {
                ongoingVisits.set(i, DomainToOngoingVisitModel.map(updated));
                return;
            }
        }
        throw new NoSuchElementException(String.format("OngoingVisit with studentId %d not found", updated.getStudentId()));
    }

}
