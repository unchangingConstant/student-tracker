package io.github.unchangingconstant.studenttracker.app.models;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.domain.OngoingVisitDomain;
import io.github.unchangingconstant.studenttracker.app.mappers.model.DomainToOngoingVisitModelMapper;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.services.Observer;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 *  TODO make it so it updates if any field in the ongoingVisit model is updated behind the scenes
 *  (Student name, subjects, etc.) 
 */
@Singleton
public class OngoingVisitTableModel {

    private SimpleListProperty<OngoingVisitModel> ongoingVisits;
    private AttendanceService attendanceService;

    @Inject
    public OngoingVisitTableModel(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
        Collection<OngoingVisitDomain> initialData = attendanceService.getOngoingVisits().values();
        ongoingVisits = new SimpleListProperty<>(FXCollections.observableArrayList());
        initialData.forEach(domain -> ongoingVisits.add(DomainToOngoingVisitModelMapper.map(domain)));
        Observer<OngoingVisitDomain> observer = attendanceService.getOngoingVisitsObserver();
        observer.subscribeToDeletes(visits -> onOngoingVisitDelete(visits));
        observer.subscribeToInserts(visits -> onOngoingVisitInsert(visits));
        observer.subscribeToUpdates(visits -> onOngoingVisitUpdate(visits));
    }

    public ObservableList<OngoingVisitModel> ongoingVisits() {
        return FXCollections.unmodifiableObservableList(ongoingVisits);
    }

    // This is okay since visits are immutable
    public OngoingVisitModel get(Integer studentId) {
        for (OngoingVisitModel visit : ongoingVisits) {
            if (visit.getStudentId().getValue().equals(studentId)) {
                return visit;
            }
        }
        throw new NoSuchElementException(String.format("Visit with studentId %d could not be found", studentId));
    }

    public void bindProperty(Property<ObservableList<OngoingVisitModel>> prop) {
        prop.bind(ongoingVisits);
    }

    private void onOngoingVisitDelete(List<OngoingVisitDomain> deletedVisits) {
        deletedVisits.forEach(deleted -> {
        Boolean removed = ongoingVisits.removeIf(ongoingVisit -> ongoingVisit.getStudentId().get() == deleted.getStudentId());
            if (!removed) {
                // TODO this will prevent all other visits in the list from being processed
                throw new NoSuchElementException(String.format("Tried to delete OngoingVisit with studentId %d but it could not be found", deleted.getStudentId()));
            }
        });
    }

    private void onOngoingVisitInsert(List<OngoingVisitDomain> insertedVisits) {
        insertedVisits.forEach(inserted -> {
            // TODO Service can't provide all OngoingVisitDomain info which is why the model must pull from the disk
            // Find some solution to this. A cache for the AttendanceService is becoming an increasingly good idea
            OngoingVisitModel newVisit = DomainToOngoingVisitModelMapper.map(attendanceService.getOngoingVisit(inserted.getStudentId()));
            ongoingVisits.add(newVisit);
        });
    }

    private void onOngoingVisitUpdate(List<OngoingVisitDomain> updatedVisits) {
        updatedVisits.forEach(updated -> {
            for (int i = 0; i < ongoingVisits.size(); i++){
                if (ongoingVisits.get(i).getStudentId().getValue().equals(updated.getStudentId())) {
                    ongoingVisits.set(i, DomainToOngoingVisitModelMapper.map(updated));
                    return;
                }
            }
            // TODO this will keep all other updates from being processed
            throw new NoSuchElementException(String.format("Tired to update OngoingVisit with studentId %d but it could not be found", updated.getStudentId()));
        });
    }

}
