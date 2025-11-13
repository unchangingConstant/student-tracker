package io.github.unchangingconstant.studenttracker.app.domain;

import java.time.Instant;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class VisitDomain {

    @NonNull
    private Integer visitId;
    @NonNull
    private Integer studentId;
    @NonNull
    private Instant startTime;
    @NonNull
    private Instant endTime;

}
