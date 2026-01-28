package com.github.unchangingconstant.studenttracker.app.entities;

import java.time.Instant;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Visit {

    Integer visitId;
    Integer studentId;
    Instant startTime;
    Integer duration;

    public static boolean validate(Visit visit) {
        boolean studentIdValid =
            visit.getStudentId() != null;
        boolean startTimeValid =
            visit.getStartTime() != null;
        boolean durationValid =
            visit.getDuration() != null &&
            visit.getDuration() >= 0;

        return studentIdValid && startTimeValid && durationValid;
    }

}
