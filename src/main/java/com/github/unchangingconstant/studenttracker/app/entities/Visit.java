package com.github.unchangingconstant.studenttracker.app.entities;

import java.time.Instant;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
@Builder
public class Visit {

    @NotNull
    @Min(value = 1)
    Integer visitId;

    @NotNull
    @Min(value = 1)
    Integer studentId;

    @NotNull
    Instant startTime;

    @NotNull
    @Min(value = 0)
    Integer duration;

}
