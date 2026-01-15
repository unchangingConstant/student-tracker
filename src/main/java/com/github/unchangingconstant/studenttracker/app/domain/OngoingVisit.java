package com.github.unchangingconstant.studenttracker.app.domain;

import java.time.Instant;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OngoingVisit {
    
    private Integer studentId;
    private Instant startTime;
    private Integer subjects;
    private String studentName;

}
