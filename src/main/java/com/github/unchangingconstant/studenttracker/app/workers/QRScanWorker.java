package com.github.unchangingconstant.studenttracker.app.workers;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.unchangingconstant.studenttracker.app.domain.OngoingVisitDomain;
import com.github.unchangingconstant.studenttracker.app.domain.StudentQRCode;
import com.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import com.github.unchangingconstant.studenttracker.app.services.KeyLoggerService;
import com.github.unchangingconstant.studenttracker.threads.ThreadManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * What is a worker?
 * 
 * I figure services are components called by some client. Workers are not called, they
 * do all of their work autonomously in the background.
 * 
 * This way, when I leave methods public in workers, it's still apparent that they're not
 * meant to be called by the client. They are meant to be called for testing, however.
 */
/**
 * QRCodes will represent studentIds via a decimal number followed by a hex number.
 * 
 * An example of a QRCode string will look like this:
 * 
 * STA1x1STA OR STA266x10aSTA OR STA12xcSTA
 * 
 * This service will be subscribed to the global key event logger (GlobalScreen, from 
 * jnativehook), track the last 26 characters typed and search for QR patterns upon an
 * ENTER key press
 */
@Singleton
public class QRScanWorker {

    private final LinkedBlockingDeque<Character> keyCharBuffer = new LinkedBlockingDeque<>(26);

    private AttendanceService attendanceService;

    @Inject
    public QRScanWorker(AttendanceService attendanceService, KeyLoggerService keyLoggerService) {
        this.attendanceService = attendanceService;
		keyLoggerService.addNativeKeyListener(new KeyEventHook());
    }

    public String findQRCode(LinkedBlockingDeque<Character> buffer) {
        String bufferStr = buffer.stream() // converts buffer to a string
            .map(String::valueOf)
            .collect(Collectors.joining())
            .toLowerCase();
        return StudentQRCode.extractQrCode(bufferStr);
    }

    public void processQRCode(String qrCode) {
        if (!StudentQRCode.validate(qrCode)) {
            return;
        }
        // If valid, extracts studentId from qrCode
        Matcher decMatcher = Pattern.compile(StudentQRCode.DECIMAL_REGEX + StudentQRCode.SEPERATOR).matcher(qrCode);
        Integer studentId = Integer.valueOf(decMatcher.group().replace(StudentQRCode.SEPERATOR, ""));
        // Keeps the service from being called by the jnativehook thread
        ThreadManager.mainBackgroundExecutor().submit(() -> {
            OngoingVisitDomain ongoingVisit = attendanceService.getOngoingVisit(studentId);
            if (ongoingVisit == null) {
                attendanceService.startOngoingVisit(studentId);
            } else {
                attendanceService.endOngoingVisit(studentId, ongoingVisit.getStartTime());
            }
        });
    }

    // Public for testing purposes no mas
    // TODO put this into the KeyLoggerService
    public class KeyEventHook implements NativeKeyListener {

        @Override
        public void nativeKeyTyped(NativeKeyEvent e) {
            Character keyChar = e.getKeyChar();
            if (!keyCharBuffer.offer(keyChar)) {
                keyCharBuffer.removeFirst();
                keyCharBuffer.add(keyChar);
            }
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
