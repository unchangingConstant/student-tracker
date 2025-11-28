package io.github.unchangingconstant.studenttracker.app.domain;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

// See issue #16
@Data
@Builder
public class StudentDomain {

    private Integer studentId;
    private String fullLegalName;
    private String prefName;
    private Integer subjects;
    private Instant dateAdded;

}
