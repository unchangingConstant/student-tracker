package io.github.unchangingconstant.studenttracker.app.models;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongPropertyBase;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;

public class OngoingVisitModel {

    private ReadOnlyObjectWrapper<ZonedDateTime> startTime;
    private ReadOnlyLongWrapper timeRemaining;
    @Getter
    private ReadOnlyStringProperty studentName;
    @Getter
    private Integer visitId;
    private Integer subjects;
    private Timer timer;

    public OngoingVisitModel(String studentName, ZonedDateTime startTime, Integer visitId, Integer subjects) {
        this.timer = new Timer(true);
        this.timeRemaining = new ReadOnlyLongWrapper(subjects * 30);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // CLAUDE CODE, what is Platform.runLater()???
                // TODO find out. I think concurrency is unavoidable anyway
                Platform.runLater(() -> {
                    timeRemaining
                            .set((30 * subjects)
                                    - ChronoUnit.MINUTES.between(LocalDateTime.now(), startTime));
                });
            }
        }, 0, 1000);

        this.studentName = new SimpleStringProperty(studentName);
        this.startTime = new ReadOnlyObjectWrapper<>(startTime);
        this.visitId = visitId;
        this.subjects = subjects;
    }

}
