package io.github.unchangingconstant.studenttracker.app.models;

import java.time.Instant;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;

@Getter
public class OngoingVisitModel {
    private final SimpleIntegerProperty studentId;
    private final SimpleObjectProperty<Instant> startTime;
    private final SimpleStringProperty studentName;

    public OngoingVisitModel(Integer studentId, Instant startTime, String studentName)  {
        this.studentId = new SimpleIntegerProperty(studentId);
        this.startTime = new SimpleObjectProperty<>(startTime);
        this.studentName = new SimpleStringProperty(studentName);
    }
}
