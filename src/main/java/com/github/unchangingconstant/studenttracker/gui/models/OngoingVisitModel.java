package com.github.unchangingconstant.studenttracker.gui.models;

import java.time.Instant;

import com.github.unchangingconstant.studenttracker.app.entities.OngoingVisit;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;

@Getter
public class OngoingVisitModel {
    private final SimpleIntegerProperty studentId;
    private final SimpleObjectProperty<Instant> startTime;
    private final SimpleLongProperty timeRemaining;
    private final SimpleIntegerProperty visitTime;
    private final SimpleStringProperty studentName;

    public OngoingVisitModel(Integer studentId, Instant startTime, Long timeRemaining, Integer visitTime, String studentName)  {
        this.studentId = new SimpleIntegerProperty(studentId);
        this.startTime = new SimpleObjectProperty<>(startTime);
        this.timeRemaining = new SimpleLongProperty(timeRemaining);
        this.visitTime = new SimpleIntegerProperty(visitTime);
        this.studentName = new SimpleStringProperty(studentName);
    }

    /*
     * OngoingVisit model is more of a utility data class than a direct reflection of the domain entities
     * This model is designed specifically with the ListAttendanceDashboard in mind.
     */
    public static OngoingVisitModel map(OngoingVisit entity, Long timeRemaining, Integer visitTime, String studentName) {
        return new OngoingVisitModel(
            entity.getStudentId(),
            entity.getStartTime(),
            timeRemaining,
            visitTime,
            studentName);
    }

    /*
     * Collection bindings will need this to know which entities are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OngoingVisitModel) {
            return (((OngoingVisitModel) obj).getStudentId().get() == studentId.get());
        }
        return false;
    }
}
