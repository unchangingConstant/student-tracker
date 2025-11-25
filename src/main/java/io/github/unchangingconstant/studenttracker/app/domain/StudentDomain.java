package io.github.unchangingconstant.studenttracker.app.domain;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class StudentDomain {

    private Integer studentId;
    @NonNull
    private String fullLegalName;
    @NonNull // Cannot be null but CAN be empty
    private String prefName;
    @NonNull
    private Integer subjects;
    private Instant dateAdded;

}
