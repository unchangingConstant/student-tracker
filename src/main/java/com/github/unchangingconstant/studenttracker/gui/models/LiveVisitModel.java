package com.github.unchangingconstant.studenttracker.gui.models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.github.unchangingconstant.studenttracker.app.entities.OngoingVisit;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;

@Getter
public class LiveVisitModel {
    private final SimpleIntegerProperty studentId;
    private final SimpleObjectProperty<Instant> startTime;
    private final SimpleLongProperty timeRemaining;
    private final SimpleIntegerProperty visitTime;
    private final SimpleStringProperty studentName;

    public LiveVisitModel(OngoingVisit ongoingVisit, Long timeRemaining, StudentModel studentModel) {
        this.studentId = new SimpleIntegerProperty(ongoingVisit.getStudentId());
        this.startTime = new SimpleObjectProperty<Instant>(ongoingVisit.getStartTime());
        this.timeRemaining = new SimpleLongProperty(timeRemaining);
        this.visitTime = studentModel.getVisitTime();
        this.studentName = studentModel.getFullName();
    }

    /*
     * Collection bindings will need this to know which entities are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LiveVisitModel) {
            return (((LiveVisitModel) obj).getStudentId().get() == studentId.get());
        }
        return false;
    }
}
