package com.github.unchangingconstant.studenttracker.gui.models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.github.unchangingconstant.studenttracker.app.entities.OngoingVisit;
import com.github.unchangingconstant.studenttracker.app.dbmanager.AttendanceObserver;
import com.github.unchangingconstant.studenttracker.app.dbmanager.AttendanceRecordManager;
import com.github.unchangingconstant.studenttracker.gui.utils.MapToListBinding;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 *  TODO make it so it updates if any field in the ongoingVisit model is updated behind the scenes
 *  (Student name, subjects, etc.) 
 */
@Singleton
public class OngoingVisitTableModel {

    private final SimpleMapProperty<Integer, OngoingVisitModel> ongoingVisits;
    private final MapToListBinding<Integer, OngoingVisitModel> ongoingVisitList;

    private final StudentTableModel studentTableModel;

    @Inject
    public OngoingVisitTableModel(AttendanceRecordManager attendanceService, StudentTableModel studentTableModel) {
        // Populates table with data from RecordManager
        Collection<OngoingVisit> initialData = attendanceService.getOngoingVisits();
        ongoingVisits = new SimpleMapProperty<>(FXCollections.observableHashMap());
        initialData.forEach(ongoingVisit -> {
            StudentModel student = studentTableModel.getStudent(ongoingVisit.getStudentId());
            ongoingVisits.put(ongoingVisit.getStudentId(),
                OngoingVisitModel.map(
                    ongoingVisit,
                    (long) student.getVisitTime().get(),
                    (int) (student.getVisitTime().get() - ChronoUnit.MINUTES.between(ongoingVisit.getStartTime(), Instant.now())),
                    student.getFullName().get()));
        });
        ongoingVisitList = new MapToListBinding<>(ongoingVisits);
        AttendanceObserver<OngoingVisit> observer = attendanceService.getOngoingVisitsObserver();
        /**
         * These Runnables will be called from the background thread and potentially
         * affect the JavaFX thread. So, Platform.runLater() is necessary here.
         */
        observer.subscribeToDeletes(visits -> Platform.runLater(() -> onOngoingVisitDelete(visits)));
        observer.subscribeToInserts(visits -> Platform.runLater(() -> onOngoingVisitInsert(visits)));
        observer.subscribeToUpdates(visits -> Platform.runLater(() -> onOngoingVisitUpdate(visits)));

        this.studentTableModel = studentTableModel;
    }

    // This is okay since visits are immutable
    public Optional<OngoingVisitModel> find(Integer studentId) {
        return Optional.ofNullable(ongoingVisits.get(studentId));
    }

    public void bindList(Property<ObservableList<OngoingVisitModel>> list) {
        list.bind(ongoingVisitList);
    }

    private void onOngoingVisitDelete(List<OngoingVisit> deletedVisits) {
        deletedVisits.forEach(deleted -> {
            ongoingVisits.remove(deleted.getStudentId());
        });
    }

    private void onOngoingVisitInsert(List<OngoingVisit> insertedVisits) {
        insertedVisits.forEach(inserted -> {
            ongoingVisits.put(inserted.getStudentId(), OngoingVisitModel.map(inserted));
        });
    }

    private void onOngoingVisitUpdate(List<OngoingVisit> updatedVisits) {
        updatedVisits.forEach(updated -> {
            ongoingVisits.get(updated.getStudentId()).getStartTime().set(updated.getStartTime());
        });
    }

}
