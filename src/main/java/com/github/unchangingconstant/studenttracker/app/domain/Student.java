package com.github.unchangingconstant.studenttracker.app.domain;

import java.time.Instant;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

// See issue #16
@Data
@Builder
public class Student {

    // Names shouldn't have trailing or leading white space
    public static final String NAME_REGEX = "^[^\\s].*[^\\s]$|^[^\\s]?$";

    @NotNull
    @Min(value = 1)
    private Integer studentId;

    @NotNull
    @Pattern(regexp = NAME_REGEX)
    @Size(min = 1, max = 150)
    private String fullLegalName;

    @Pattern(regexp = NAME_REGEX)
    @Size(min = 1, max = 150)
    private String prefName;

    // TODO should be 30 or 60, not 30 to 60. Custom annotations?
    @Min(value = 30)
    @Max(value = 60)
    private Integer visitTime;

    @NotNull
    @PastOrPresent
    private Instant dateAdded;

}
