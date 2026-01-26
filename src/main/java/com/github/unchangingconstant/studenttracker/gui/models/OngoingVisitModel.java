package com.github.unchangingconstant.studenttracker.gui.models;

import java.time.Instant;

import com.github.unchangingconstant.studenttracker.app.entities.OngoingVisit;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

@Getter
public class OngoingVisitModel {
    private final SimpleIntegerProperty studentId;
    private final SimpleObjectProperty<Instant> startTime;

    public OngoingVisitModel(Integer studentId, Instant startTime)  {
        this.studentId = new SimpleIntegerProperty(studentId);
        this.startTime = new SimpleObjectProperty<>(startTime);
    }

    public static OngoingVisitModel map(OngoingVisit entity) {
        return new OngoingVisitModel(entity.getStudentId(), entity.getStartTime());
    }
}
