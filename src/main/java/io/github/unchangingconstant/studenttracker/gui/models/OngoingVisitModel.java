package io.github.unchangingconstant.studenttracker.gui.models;

import java.time.Instant;

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
    private final SimpleIntegerProperty subjects;
    private final SimpleStringProperty studentName;

    public OngoingVisitModel(Integer studentId, Instant startTime, Long timeRemaining, Integer subjects, String studentName)  {
        this.studentId = new SimpleIntegerProperty(studentId);
        this.startTime = new SimpleObjectProperty<>(startTime);
        this.timeRemaining = new SimpleLongProperty(timeRemaining);
        this.subjects = new SimpleIntegerProperty(subjects);
        this.studentName = new SimpleStringProperty(studentName);
    }
}
