package com.github.unchangingconstant.studenttracker.app.qrscan.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.github.unchangingconstant.studenttracker.app.entities.StudentQRCode;

public class QRScanUtils {

    public static void addToKeyBuffer(LinkedBlockingDeque<Character> buffer, Character keyChar) {
        if (!buffer.offer(keyChar)) {
            buffer.removeFirst();
            buffer.add(keyChar);
        }
    }

    public static Integer extractIDFromQR(String qrCode) {
        Matcher idMatcher = Pattern
                .compile(StudentQRCode.HEADER + StudentQRCode.ID_REGEX + StudentQRCode.SEPERATOR)
                .matcher(qrCode);
        if (idMatcher.find()) {
            String idStr = removeCharSeqs(idMatcher.group(), StudentQRCode.HEADER,
                    StudentQRCode.SEPERATOR);
            return Integer.valueOf(idStr);
        }
        return null;
    }

    public static Integer extractChecksumValueFromQR(String qrCode) {
        Matcher checksumMatcher = Pattern
                .compile(
                        StudentQRCode.SEPERATOR + StudentQRCode.CHECKSUM_REGEX + StudentQRCode.FOOTER)
                .matcher(qrCode);
        if (checksumMatcher.find()) {
            String checksumStr = removeCharSeqs(checksumMatcher.group(), StudentQRCode.SEPERATOR,
                    StudentQRCode.FOOTER);
            return Integer.valueOf(checksumStr, 16);
        }
        return null;
    }

    private static String removeCharSeqs(String str, String... charSeqs) {
        String result = str;
        for (String charSeq : charSeqs) {
            result = result.replace(charSeq, "");
        }
        return result;
    }

}
