package com.github.unchangingconstant.studenttracker.app.domain;

/**
 * Specification for QRCodes scannable by the application
 */
public class StudentQRCodeDomain {
    
    // Rewrite these into validation methods?
    public static final String HEADER = "sta";
    public static final String SEPERATOR = "x";
    public static final String FOOTER = "sta";
    public static final Integer MAX_ID = Integer.MAX_VALUE;

    public static final String DECIMAL_REGEX = "(\\d{1,10})";
    public static final String HEX_REGEX = "([0-9a-f]{1,8})";

    public static final String QR_CODE_REGEX = 
        String.format("%s%s%s%s%s",
            HEADER, DECIMAL_REGEX, SEPERATOR, HEX_REGEX, FOOTER
        );

    public static final Integer MAX_QR_LEN = 25;


}
