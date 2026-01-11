package com.github.unchangingconstant.studenttracker.app.domain;

import java.time.Instant;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Value;

// See issue #16
@Value
@Builder
public class Student {

    // Names shouldn't have trailing or leading white space
    public static final String nameRegEx = "^[^\\s].*[^\\s]$|^[^\\s]?$";

    @NotNull
    @Min(value = 1, message = "Student ID must be at least 1")
    private Integer studentId;

    @NotNull
    @Pattern(regexp = nameRegEx, message = "Names must have no trailing or leading white space")
    @Size(min = 1, max = 150, 
        message = "Full legal name must be between 1 and 150 characters")
    private String fullLegalName;

    @Pattern(regexp = nameRegEx, message = "Names must have no trailing or leading white space")
    @Size(min = 1, max = 150, 
        message = "Preferred name must be between 1 and 150 characters")
    private String prefName;

    @Min(value = 30, message = "Visit time must be at least 30 minutes")
    @Max(value = 60, message = "Visit time must be at most 60 minutes")
    private Integer visitTime;

    @NotNull
    @PastOrPresent(message = "Student cannot have been added in the future")
    private Instant dateAdded;

}
