package io.github.unchangingconstant.studenttracker.app.backend.entities;

import java.time.Instant;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Visit {

    @NonNull
    private Integer visitId;
    @NonNull
    private Integer studentId;
    @NonNull
    private Instant startTime;
    // If null, the visit is ongoing
    private Instant endTime;

}
