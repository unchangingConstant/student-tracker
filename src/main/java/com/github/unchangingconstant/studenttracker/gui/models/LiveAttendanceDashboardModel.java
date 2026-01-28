package com.github.unchangingconstant.studenttracker.gui.models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.unchangingconstant.studenttracker.app.entities.OngoingVisit;
import com.github.unchangingconstant.studenttracker.app.dbmanager.AttendanceObserver;
import com.github.unchangingconstant.studenttracker.app.dbmanager.AttendanceRecordManager;
import com.github.unchangingconstant.studenttracker.app.entities.Student;
import com.github.unchangingconstant.studenttracker.gui.utils.MapToListBinding;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Singleton
public class LiveAttendanceDashboardModel {

    private final SimpleMapProperty<Integer, LiveVisitModel> ongoingVisits;
    private final MapToListBinding<Integer, LiveVisitModel> ongoingVisitList;

    private final StudentTableModel studentTableModel;

    @Inject
    public LiveAttendanceDashboardModel(AttendanceRecordManager attendanceService, StudentTableModel studentTableModel) {
        // Populates table with data from RecordManager
        Map<Integer, OngoingVisit> initialData = attendanceService.getOngoingVisits();
        ongoingVisits = new SimpleMapProperty<>(FXCollections.observableHashMap());
        initialData.values().forEach(ongoingVisit -> {
            StudentModel student = studentTableModel.getStudent(ongoingVisit.getStudentId());
            ongoingVisits.put(ongoingVisit.getStudentId(),
                new LiveVisitModel(
                    ongoingVisit,
                    student.getVisitTime().get() - ChronoUnit.MINUTES.between(ongoingVisit.getStartTime(), Instant.now()),
                    student));
        });
        ongoingVisitList = new MapToListBinding<>(ongoingVisits);
        AttendanceObserver<OngoingVisit> ongoingVisitObserver = attendanceService.getOngoingVisitsObserver();
        /*
         * These Runnables will be called from the background thread and potentially
         * affect the JavaFX thread. So, Platform.runLater() is necessary here.
         */
        ongoingVisitObserver.subscribeToDeletes(visits -> Platform.runLater(() -> onOngoingVisitDelete(visits)));
        ongoingVisitObserver.subscribeToInserts(visits -> Platform.runLater(() -> onOngoingVisitInsert(visits)));
        ongoingVisitObserver.subscribeToUpdates(visits -> Platform.runLater(() -> onOngoingVisitUpdate(visits)));

        this.studentTableModel = studentTableModel;
    }

    public ObservableList<LiveVisitModel> unmodifiableOngoingVisitList() {
        return FXCollections.unmodifiableObservableList(ongoingVisitList);
    }

    public LiveVisitModel get(Integer studentId) {
        return ongoingVisits.get(studentId);
    }

    public void bindList(Property<ObservableList<LiveVisitModel>> list) {
        list.bind(ongoingVisitList);
    }

    private void onOngoingVisitDelete(List<OngoingVisit> deletedVisits) {
        deletedVisits.forEach(deleted -> {
            ongoingVisits.remove(deleted.getStudentId());
        });
    }

    private void onOngoingVisitInsert(List<OngoingVisit> insertedVisits) {
        Instant now = Instant.now();
        insertedVisits.forEach(inserted -> {
            StudentModel student = studentTableModel.getStudent(inserted.getStudentId());
            ongoingVisits.put(inserted.getStudentId(), new LiveVisitModel(
                inserted,
                student.getVisitTime().get() - ChronoUnit.MINUTES.between(inserted.getStartTime(), now),
                student));
        });
    }

    private void onOngoingVisitUpdate(List<OngoingVisit> updatedVisits) {
        updatedVisits.forEach(updated -> {
            ongoingVisits.get(updated.getStudentId()).getStartTime().set(updated.getStartTime());
        });
    }

}
