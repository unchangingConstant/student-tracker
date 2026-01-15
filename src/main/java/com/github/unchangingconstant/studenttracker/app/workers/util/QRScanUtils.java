package com.github.unchangingconstant.studenttracker.app.workers.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.unchangingconstant.studenttracker.app.domain.StudentQRCode;

public class QRScanUtils {

    public static void addToKeyBuffer(LinkedBlockingDeque<Character> buffer, Character keyChar) {
        if (!buffer.offer(keyChar)) {
            buffer.removeFirst();
            buffer.add(keyChar);
        }
    }

    public static Integer extractIDFromQR(String qrCode) {
        Matcher idMatcher = Pattern
                .compile(StudentQRCodeDomain.HEADER + StudentQRCodeDomain.ID_REGEX + StudentQRCodeDomain.SEPERATOR)
                .matcher(qrCode);
        if (idMatcher.find()) {
            String idStr = removeCharSeqs(idMatcher.group(), StudentQRCodeDomain.HEADER,
                    StudentQRCodeDomain.SEPERATOR);
            return Integer.valueOf(idStr);
        }
        return null;
    }

    public static Integer extractChecksumValueFromQR(String qrCode) {
        Matcher checksumMatcher = Pattern
                .compile(
                        StudentQRCodeDomain.SEPERATOR + StudentQRCodeDomain.CHECKSUM_REGEX + StudentQRCodeDomain.FOOTER)
                .matcher(qrCode);
        if (checksumMatcher.find()) {
            String checksumStr = removeCharSeqs(checksumMatcher.group(), StudentQRCodeDomain.SEPERATOR,
                    StudentQRCodeDomain.FOOTER);
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
