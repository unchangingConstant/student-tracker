package io.github.unchangingconstant.studenttracker.app.backend.entities;

import java.time.Instant;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class OngoingVisit {
    
    @NonNull
    private Integer studentId;
    @NonNull
    private Instant startTime;
    @NonNull
    private String studentName;

}
