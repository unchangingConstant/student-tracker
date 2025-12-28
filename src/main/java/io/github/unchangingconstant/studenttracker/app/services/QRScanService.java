package io.github.unchangingconstant.studenttracker.app.services;

import java.util.concurrent.LinkedBlockingDeque;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * QRCodes will represent studentIds via a decimal number followed by a hex number.
 * 
 * An example of a QRCode string will look like this:
 * 
 * STA1x1 OR STA257x101
 */
@Singleton
public class QRScanService {

    private final LinkedBlockingDeque<Character> keyCharBuffer = new LinkedBlockingDeque<>(25);
    private final String QR_CODE_HEADER =  "sta";

    private AttendanceService attendanceService;

    @Inject
    public QRScanService(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }
}
