package com.github.unchangingconstant.studenttracker.app.entities;

import java.time.Instant;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OngoingVisit {
    
    Integer studentId;
    Instant startTime;

}
