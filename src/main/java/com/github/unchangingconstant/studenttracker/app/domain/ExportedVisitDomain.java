package com.github.unchangingconstant.studenttracker.app.domain;

import java.time.Instant;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
// For use in ExcelExportService
// Organize these data objects better, jesus
public class ExportedVisitDomain {
    
    private String studentName;
    private Instant startTime;
    private Instant endTime;
    private Long duration;

}
