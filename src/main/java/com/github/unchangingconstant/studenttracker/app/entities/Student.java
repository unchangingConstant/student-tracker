package com.github.unchangingconstant.studenttracker.app.entities;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Builder;
import lombok.Value;

// See issue #16
@Value
@Builder
public class Student {

    // Names shouldn't have trailing or leading white space
    public static final String NAME_REGEX = "^\\S.*\\S$|^\\S?$";

    Integer studentId;

    public static final int FULL_NAME_MAX_LEN = 150;
    public static final int FULL_NAME_MIN_LEN = 1;
    String fullName;

    public static final int PREFERRED_NAME_MAX_LEN = 150;
    public static final int PREFERRED_NAME_MIN_LEN = 0;
    String preferredName;

    public static final int[] VISIT_TIME_ALLOWED_VALUES = new int[] {30, 60};
    Integer visitTime;

    Instant dateAdded;

    public static boolean validate(Student student) {
        boolean fullNameValid =
            student.getFullName() != null &&
            student.getFullName().matches(NAME_REGEX) &&
            student.getFullName().length() <= 150;
        boolean preferredNameValid =
            student.getPreferredName() != null &&
            student.getPreferredName().matches(NAME_REGEX) &&
            student.getPreferredName().length() <= 150;
        boolean visitTimeValid =
            student.getVisitTime() != null &&
            (student.getVisitTime().equals(30) ||
            student.getVisitTime().equals(60));
        boolean dateAddedValid =
            student.getDateAdded() != null;

        return fullNameValid && preferredNameValid && visitTimeValid && dateAddedValid;
    }

}
