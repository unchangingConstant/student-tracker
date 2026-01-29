package com.github.unchangingconstant.studenttracker.gui.models;

import java.util.List;

import com.github.unchangingconstant.studenttracker.app.entities.Visit;
import com.github.unchangingconstant.studenttracker.app.dbmanager.DatabaseObserver;
import com.github.unchangingconstant.studenttracker.app.dbmanager.DatabaseManager;
import com.github.unchangingconstant.studenttracker.gui.utils.MapToListBinding;
import com.github.unchangingconstant.studenttracker.gui.utils.ServiceTask;
import com.github.unchangingconstant.studenttracker.threads.ThreadManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Singleton
public class VisitTableModel {
    
    private final DatabaseManager attendanceService;

    private final SimpleMapProperty<Integer, VisitModel> visits;
    private final MapToListBinding<Integer, VisitModel> visitList;
    
    private final SimpleIntegerProperty currentStudent;
    public final SimpleIntegerProperty currentStudentProperty() {return currentStudent;}

    @Inject
    public VisitTableModel(DatabaseManager attendanceService) {
        this.currentStudent = new SimpleIntegerProperty(-1);
        this.visits = new SimpleMapProperty<>(FXCollections.observableHashMap());
        this.visitList = new MapToListBinding<>(visits);
        this.attendanceService = attendanceService;
        setupCurrentStudentProperty();

        DatabaseObserver<Visit> obs = attendanceService.getVisitsObserver();
        /**
         * These Runnables will be called from the background thread and potentially
         * affect the JavaFX thread. So, Platform.runLater() is necessary here.
         */
        obs.subscribeToInserts(visits -> Platform.runLater(() -> onVisitInsert(visits)));
        obs.subscribeToDeletes(visits -> Platform.runLater(() -> onVisitDelete(visits)));
        obs.subscribeToUpdates(visits -> Platform.runLater(() -> onVisitUpdate(visits)));
    }

    public ObservableList<VisitModel> unmodifiableVisitList() {
        return FXCollections.unmodifiableObservableList(visitList);
    }

    public void bindList(Property<ObservableList<VisitModel>> list) {
        list.bind(visitList);
    }

    private void onVisitInsert(List<Visit> insertedVisits) {
        insertedVisits.forEach(visit -> {
            if (!visit.getStudentId().equals(currentStudent.get())) return;
            visits.put(visit.getVisitId(), new VisitModel(visit));
        });
    }

    private void onVisitDelete(List<Visit> deletedVisits) {
        deletedVisits.forEach(visit -> {
            if (!visit.getStudentId().equals(currentStudent.get())) return;
            visits.remove(visit.getVisitId());
        });
    }

    private void onVisitUpdate(List<Visit> updatedVisits) {
        updatedVisits.forEach(updatedVisit -> {
            if (!updatedVisit.getStudentId().equals(currentStudent.get())) return;
            visits.get(updatedVisit.getVisitId()).update(updatedVisit);
        });
    }

    private void setupCurrentStudentProperty() {
        currentStudent.addListener((obs, oldVal, newVal) -> {
            visits.clear();
            if (!newVal.equals(-1)) {
                ThreadManager.mainBackgroundExecutor().submit(new ServiceTask<Void>() {
                    @Override
                    protected Void call() throws Exception {
                    List<Visit> studentVisits = attendanceService.findVisitsWithStudentId(newVal.intValue());
                    Platform.runLater(() -> {
                        studentVisits.forEach(visit -> visits.put(visit.getVisitId(), new VisitModel(visit)));
                    });
                    return null;
                    }
                });
            }
        });
    }

}
