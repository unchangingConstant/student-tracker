package io.github.unchangingconstant.studenttracker.app.domain;

import java.time.Instant;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class StudentDomain {

    @NonNull
    private Integer studentId;
    @NonNull
    private String fullLegalName;
    @NonNull // Cannot be null but CAN be empty
    private String prefName;
    @NonNull
    private Integer subjects;
    @NonNull
    private Instant dateAdded;

}
