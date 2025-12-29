package io.github.unchangingconstant.studenttracker.app.workers;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.threads.ThreadManager;

/**
 * What is a worker?
 * 
 * I figure services are components called by some client. Workers do all of their work
 * autonomously in the background.
 * 
 * This way, when I leave methods public in workers, it's still apparent that they're not
 * meant to be called by the client. They are meant to be called for testing, however.
 */
/**
 * QRCodes will represent studentIds via a decimal number followed by a hex number.
 * 
 * An example of a QRCode string will look like this:
 * 
 * STA1x1 OR STA257x101
 * 
 * This service will be subscribed to the global key event logger (GlobalScreen, from 
 * jnativehook) track the last 25 characters typed and search for QR patterns upon an
 * ENTER key press
 */
@Singleton
public class QRScanWorker {

    private final LinkedBlockingDeque<Character> keyCharBuffer = new LinkedBlockingDeque<>(25);
    private final String QR_CODE_HEADER =  "sta";

    private AttendanceService attendanceService;

    @Inject
    public QRScanWorker(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
		GlobalScreen.addNativeKeyListener(new KeyEventHook());
        System.out.println("Created!");
    }

    public String findQRCode() {
        String bufferStr = keyCharBuffer.stream() // converts buffer to a string
            .map(String::valueOf)
            .collect(Collectors.joining())
            .toLowerCase();
        // Checks that the QRCode format is right
        Matcher matcher = Pattern.compile(QR_CODE_HEADER + "[0-9]x[0-9a-e]").matcher(bufferStr);
        if (matcher.find()) { // Dunno if this good or not, honestly
            return matcher.group();
        }
        return null;
    }

    public void processQRCode(String qrCode) {
        // Searches for leading decimal number
        Matcher decMatcher = Pattern.compile("[0-9]x").matcher(qrCode);
        // Searches for trailing hex number
        Matcher hexMatcher = Pattern.compile("x[0-9a-e]").matcher(qrCode);

        if (!decMatcher.find() || !hexMatcher.find()) {
            System.out.println("QRScan format incorrect");
            return;
        }

        Integer decStudentId = Integer.valueOf(decMatcher.group().replace("x", ""));
        Integer hexStudentId = Integer.valueOf(hexMatcher.group().replace("x", ""));

        if (decStudentId != hexStudentId) {
            System.out.println("QRScan validation failed");
            return;
        }

        // Keeps the service from being called by the jnativehook thread
        ThreadManager.mainBackgroundExecutor().submit(() -> {
            attendanceService.startOngoingVisit(decStudentId);
        });
    }

    // Public for testing purposes no mas
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
                String qrCode = findQRCode();
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
