package com.github.unchangingconstant.studenttracker.app.entities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Specification / descriptor for QRCodes scannable by the application
 */
public class StudentQRCode {

    // Rewrite these into validation methods?
    public static final String HEADER = "sta";
    public static final String SEPERATOR = "x";
    public static final String FOOTER = "sta";
    public static final Integer MAX_ID = Integer.MAX_VALUE;

    public static final String ID_REGEX = "(\\d{1,10})";
    public static final String CHECKSUM_REGEX = "([0-9a-f]{1,8})";

    public static final String QR_CODE_REGEX = 
        String.format("%s%s%s%s%s",
            HEADER, ID_REGEX, SEPERATOR, CHECKSUM_REGEX, FOOTER
        );

    public static final Integer MAX_QR_LEN = 25;

    public static String createQrCode(Integer studentId) {
        return StudentQRCode.HEADER 
            + String.valueOf(studentId) 
            + StudentQRCode.SEPERATOR 
            + Integer.toHexString(studentId) 
            + StudentQRCode.FOOTER;
    }

    public static boolean validate(String qrCode) {
        // Searches for leading decimal number
        Matcher decMatcher = Pattern.compile(StudentQRCode.ID_REGEX + StudentQRCode.SEPERATOR).matcher(qrCode);
        // Searches for trailing hex number
        Matcher hexMatcher = Pattern.compile(StudentQRCode.SEPERATOR + StudentQRCode.CHECKSUM_REGEX).matcher(qrCode);

        Integer decStudentId = Integer.valueOf(decMatcher.group().replace(StudentQRCode.SEPERATOR, ""));
        Integer hexStudentId = Integer.valueOf(hexMatcher.group().replace(StudentQRCode.SEPERATOR, ""), 16);

        return decStudentId == hexStudentId;
    }

    // DELETE THIS
    public static String extractQrCode(String str) {
        // Checks that the QRCode format is right
        Matcher matcher = Pattern.compile(StudentQRCode.QR_CODE_REGEX).matcher(str);
        if (matcher.find()) { // Dunno if this good or not, honestly
            return matcher.group();
        }
        return null;
    }

}
