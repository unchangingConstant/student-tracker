package com.github.unchangingconstant.studenttracker.app.domain;

import java.time.Instant;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class OngoingVisitDomain {
    
    private Integer studentId;
    private Instant startTime;
    private Integer subjects;
    private String studentName;

}
