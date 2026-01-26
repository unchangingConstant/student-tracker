package com.github.unchangingconstant.studenttracker.gui.models;

import java.time.Instant;

import com.github.unchangingconstant.studenttracker.app.entities.Visit;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

@Getter
public class VisitModel {
    
    private final SimpleObjectProperty<Integer> visitId;
    private final SimpleObjectProperty<Instant> startTime;
    private final SimpleIntegerProperty duration;
    private final SimpleIntegerProperty studentId;

    public VisitModel(Integer visitId, Instant startTime, Integer duration, Integer studentId) {
        this.visitId = new SimpleObjectProperty<>(visitId);
        this.startTime = new SimpleObjectProperty<>(startTime);
        this.duration = new SimpleIntegerProperty(duration);
        this.studentId = new SimpleIntegerProperty(studentId);
    }

    public static VisitModel map(Visit entity) {
        return new VisitModel(
            entity.getVisitId(),
            entity.getStartTime(),
            entity.getDuration(),
            entity.getStudentId()
        );
    }
}
