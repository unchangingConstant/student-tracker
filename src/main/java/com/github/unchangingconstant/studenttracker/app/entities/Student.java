package com.github.unchangingconstant.studenttracker.app.entities;

import java.time.Instant;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

// See issue #16
@Value
@Builder
public class Student {

    // Names shouldn't have trailing or leading white space
    public static final String NAME_REGEX = "^\\S.*\\S$|^\\S?$";

    @NotNull
    @Min(value = 1)
    Integer studentId;

    @NotNull
    @Pattern(regexp = NAME_REGEX)
    @Size(min = 1, max = 150)
    String fullName;

    @Pattern(regexp = NAME_REGEX)
    @Size(min = 1, max = 150)
    String preferredName;

    // TODO should be 30 or 60, not 30 to 60. Custom annotations?
    @Min(value = 30)
    @Max(value = 60)
    Integer visitTime;

    @NotNull
    @PastOrPresent
    Instant dateAdded;

}
