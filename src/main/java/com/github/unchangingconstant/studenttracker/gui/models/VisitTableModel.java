package com.github.unchangingconstant.studenttracker.gui.models;

import java.util.List;

import com.github.unchangingconstant.studenttracker.app.domain.VisitDomain;
import com.github.unchangingconstant.studenttracker.app.mappers.model.DomainToVisitModelMapper;
import com.github.unchangingconstant.studenttracker.app.services.AttendanceObserver;
import com.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import com.github.unchangingconstant.studenttracker.gui.utils.ServiceTask;
import com.github.unchangingconstant.studenttracker.threads.ThreadManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Singleton
public class VisitTableModel {
    
    private final AttendanceService attendanceService;

    private final SimpleListProperty<VisitModel> visits;  
    
    private final SimpleIntegerProperty currentStudent;
    public final SimpleIntegerProperty currentStudentProperty() {return currentStudent;}

    @Inject
    public VisitTableModel(AttendanceService attendanceService) {
        this.currentStudent = new SimpleIntegerProperty(-1);
        this.visits = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.attendanceService = attendanceService;
        setupCurrentStudentProperty();

        AttendanceObserver<VisitDomain> obs = attendanceService.getVisitsObserver();
        /**
         * These Runnables will be called from the background thread and potentially
         * affect the JavaFX thread. So, Platform.runLater() is necessary here.
         */
        obs.subscribeToInserts(visits -> Platform.runLater(() -> onVisitInsert(visits)));
        obs.subscribeToDeletes(visits -> Platform.runLater(() -> onVisitDelete(visits)));
        obs.subscribeToUpdates(visits -> Platform.runLater(() -> onVisitUpdate(visits)));
    }

    public ObservableList<VisitModel> getVisits() {
        return FXCollections.unmodifiableObservableList(visits.get());
    }

    public void bindProperty(Property<ObservableList<VisitModel>> prop) {
        prop.bind(visits);
    }

    private void onVisitInsert(List<VisitDomain> insertedVisits) {
        insertedVisits.forEach(visit -> {
            if (!visit.getStudentId().equals(currentStudent.get())) return;
            visits.add(DomainToVisitModelMapper.map(visit));
        });
    }

    private void onVisitDelete(List<VisitDomain> deletedVisits) {
        deletedVisits.forEach(visit -> {
            if (!visit.getStudentId().equals(currentStudent.get())) return;
            visits.removeIf(otherVisit -> visit.getStudentId().equals(otherVisit.getStudentId().get()));
        });
    }

    private void onVisitUpdate(List<VisitDomain> updatedVisits) {
        updatedVisits.forEach(updatedVisit -> {
            if (!updatedVisit.getStudentId().equals(currentStudent.get())) return;
            for (VisitModel visit: visits) {
                if (visit.getVisitId().get().equals(updatedVisit.getVisitId())) {
                    visit.getStudentId().set(updatedVisit.getStudentId());
                    visit.getStartTime().set(updatedVisit.getStartTime());
                    visit.getEndTime().set(updatedVisit.getEndTime());
                    break;
                }
            }
        });
    }

    private void setupCurrentStudentProperty() {
        currentStudent.addListener((obs, oldVal, newVal) -> {
            visits.clear();
            if (!newVal.equals(-1)) {
                ThreadManager.mainBackgroundExecutor().submit(new ServiceTask<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        List<VisitDomain> studentVisits = attendanceService.findVisitsWithStudentId(newVal.intValue());
                        Platform.runLater(() -> {
                            studentVisits.forEach(visitDomain -> visits.add(DomainToVisitModelMapper.map(visitDomain)));
                        });
                        return null;
                    }
                });
            }
        });
    }

}
