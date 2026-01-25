package com.github.unchangingconstant.studenttracker.app.qrscan;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.unchangingconstant.studenttracker.app.entities.OngoingVisit;
import com.github.unchangingconstant.studenttracker.app.entities.StudentQRCode;
import com.github.unchangingconstant.studenttracker.app.dbmanager.AttendanceRecordManager;
import com.github.unchangingconstant.studenttracker.app.qrscan.util.QRScanUtils;
import com.github.unchangingconstant.studenttracker.threads.ThreadManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/*
 * QRCodes will represent studentIds via a decimal number followed by a hex
 * number. (the checksum)
 * 
 * An example of a QRCode string will look like this:
 * 
 * STA1x1STA OR STA266x10aSTA OR STA12xcSTA
 * 
 * This service will be subscribed to the global key event logger (GlobalScreen,
 * from
 * jnativehook), track the last 26 characters typed and search for QR patterns
 * upon an
 * ENTER key press
 */
@Singleton
public class QRScanner {

    private final LinkedBlockingDeque<Character> keyCharBuffer = new LinkedBlockingDeque<>(26);

    private final AttendanceRecordManager attendanceService;

    @Inject
    public QRScanner(AttendanceRecordManager attendanceService, KeyLogger keyLogger) {
        this.attendanceService = attendanceService;
        keyLogger.addNativeKeyListener(new KeyEventHook());
    }

    public String findQRCode(LinkedBlockingDeque<Character> buffer) {
        String bufferStr = buffer.stream() // converts buffer to a string
                .map(String::valueOf)
                .collect(Collectors.joining())
                .toLowerCase();
        // Trust me on this
        // Otherwise, pressing Enter twice will scan QRCodes that have already been
        // processed
        buffer.clear();
        // Checks that the QRCode format is right
        Matcher matcher = Pattern.compile(StudentQRCode.QR_CODE_REGEX).matcher(bufferStr);
        if (matcher.find()) { // Dunno if this good or not, honestly
            return matcher.group();
        }
        return null;
    }

    public void processQRCode(String qrCode) {
        Integer id = QRScanUtils.extractIDFromQR(qrCode);
        Integer checksum = QRScanUtils.extractChecksumValueFromQR(qrCode);

        System.out.println("QRCode detected: " + qrCode);
        if (id == null || checksum == null) {
            System.out.println("QR code format incorrect");
            return;
        }

        if (!id.equals(checksum)) {
            System.out.println(String.format("QR scan invalid: ID = %d, CHECKSUM = %d", id, checksum));
            return;
        }

        // Keeps the service from being called by the jnativehook thread
        ThreadManager.mainBackgroundExecutor().submit(() -> {
            Optional<OngoingVisit> potentialOngoingVisit = attendanceService.findOngoingVisit(id);
            Instant now = Instant.now();
            if (potentialOngoingVisit.isEmpty()) {
                attendanceService.startOngoingVisit(OngoingVisit.builder().studentId(id).startTime(now).build());
            } else {
                OngoingVisit ongoingVisit = potentialOngoingVisit.get();
                Instant startTime = ongoingVisit.getStartTime();
                attendanceService.endOngoingVisit(ongoingVisit, (int) ChronoUnit.MINUTES.between(startTime, now));
            }
        });
    }

    // Public for testing purposes no mas
    public class KeyEventHook implements NativeKeyListener {

        @Override
        public void nativeKeyTyped(NativeKeyEvent e) {
            Character keyChar = e.getKeyChar();
            QRScanUtils.addToKeyBuffer(keyCharBuffer, keyChar);
        }

        @Override
        public void nativeKeyPressed(NativeKeyEvent e) {
            if (e.getKeyCode() == NativeKeyEvent.VC_ENTER) {
                String qrCode = findQRCode(keyCharBuffer);
                if (qrCode != null) {
                    processQRCode(qrCode);
                }
            }
        }

        @Override
        public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        }

    }

}
