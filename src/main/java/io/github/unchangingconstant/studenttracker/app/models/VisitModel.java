package io.github.unchangingconstant.studenttracker.app.models;

import java.time.Instant;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

@Getter
public class VisitModel {
    
    private final SimpleObjectProperty<Integer> visitId;
    private final SimpleObjectProperty<Instant> startTime;
    private final SimpleObjectProperty<Instant> endTime;
    private final SimpleIntegerProperty studentId;

    public VisitModel(Integer visitId, Instant startTime, Instant endTime, Integer studentId) {
        this.visitId = new SimpleObjectProperty<>(visitId);
        this.startTime = new SimpleObjectProperty<>(startTime);
        this.endTime = new SimpleObjectProperty<>(endTime);
        this.studentId = new SimpleIntegerProperty(studentId);
    }
}
