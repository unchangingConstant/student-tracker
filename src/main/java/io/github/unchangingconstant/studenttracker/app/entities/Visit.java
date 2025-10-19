package io.github.unchangingconstant.studenttracker.app.entities;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Visit {

    @NonNull
    private String studentName;
    @NonNull
    private Integer visitId;
    @NonNull
    private Integer studentId;
    @NonNull
    private LocalDateTime startTime;
    // If null, the visit is ongoing
    private LocalDateTime endTime;

}
