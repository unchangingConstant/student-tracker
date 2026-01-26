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

    public VisitModel(Visit entity) {
        this(
            entity.getVisitId(),
            entity.getStartTime(),
            entity.getDuration(),
            entity.getStudentId()
        );
    }

    public void update(Visit entity) {
        visitId.set(entity.getVisitId());
        startTime.set(entity.getStartTime());
        duration.set(entity.getDuration());
        studentId.set(entity.getStudentId());
    }

    /*
     * Collection bindings will need this to know which entities are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VisitModel) {
            return (((VisitModel) obj).getVisitId().get().equals(visitId.get()));
        }
        return false;
    }
}
