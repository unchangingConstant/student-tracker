package com.github.unchangingconstant.studenttracker.app.domain;

import java.time.Instant;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Visit {

    @NotNull
    @Min(value = 1)
    private Integer visitId;

    @NotNull
    @Min(value = 1)
    private Integer studentId;

    // TODO, start time should come before endtime. Express this with custom annotations

    @NotNull
    private Instant startTime;

    @NotNull
    @PastOrPresent
    private Instant endTime;

}
