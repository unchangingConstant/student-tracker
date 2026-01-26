package com.github.unchangingconstant.studenttracker.gui.models;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import com.github.unchangingconstant.studenttracker.app.entities.OngoingVisit;
import com.github.unchangingconstant.studenttracker.app.dbmanager.AttendanceObserver;
import com.github.unchangingconstant.studenttracker.app.dbmanager.AttendanceRecordManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.application.Platform;
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

    private final SimpleListProperty<OngoingVisitModel> ongoingVisits;

    @Inject
    public OngoingVisitTableModel(AttendanceRecordManager attendanceService) {
        Collection<OngoingVisit> initialData = attendanceService.getOngoingVisits();
        ongoingVisits = new SimpleListProperty<>(FXCollections.observableArrayList());
        initialData.forEach(ongoingVisit -> ongoingVisits.add(OngoingVisitModel.map(ongoingVisit)));
        AttendanceObserver<OngoingVisit> observer = attendanceService.getOngoingVisitsObserver();
        /**
         * These Runnables will be called from the background thread and potentially
         * affect the JavaFX thread. So, Platform.runLater() is necessary here.
         */
        observer.subscribeToDeletes(visits -> Platform.runLater(() -> onOngoingVisitDelete(visits)));
        observer.subscribeToInserts(visits -> Platform.runLater(() -> onOngoingVisitInsert(visits)));
        observer.subscribeToUpdates(visits -> Platform.runLater(() -> onOngoingVisitUpdate(visits)));
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

    private void onOngoingVisitDelete(List<OngoingVisit> deletedVisits) {
        deletedVisits.forEach(deleted -> {
            boolean removed = ongoingVisits.removeIf(ongoingVisit -> ongoingVisit.getStudentId().get() == deleted.getStudentId());
            if (!removed) {
                System.out.println(String.format("Tried to delete OngoingVisit with studentId %d but it could not be found", deleted.getStudentId()));
            }
        });
    }

    private void onOngoingVisitInsert(List<OngoingVisit> insertedVisits) {
        insertedVisits.forEach(inserted -> {
            ongoingVisits.add(OngoingVisitModel.map(inserted));
        });
    }

    private void onOngoingVisitUpdate(List<OngoingVisit> updatedVisits) {
        updatedVisits.forEach(updated -> {
            for (int i = 0; i < ongoingVisits.size(); i++){
                if (ongoingVisits.get(i).getStudentId().getValue().equals(updated.getStudentId())) {
                    ongoingVisits.set(i, OngoingVisitModel.map(updated));
                    return;
                }
            }
            System.out.println(String.format("Tired to update OngoingVisit with studentId %d but it could not be found", updated.getStudentId()));
        });
    }

}
