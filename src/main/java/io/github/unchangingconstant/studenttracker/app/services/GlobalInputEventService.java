package io.github.unchangingconstant.studenttracker.app.services;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * QRCodes will represent studentIds via a decimal number followed by a hex number.
 * 
 * An example of a QRCode string will look like this:
 * 
 * STA1x1 OR STA257x101
 * 
 * Whenever "Enter" is hit, the service will check for an "STA" and scan the successive 
 * decimal number and hex number.
 *
 * Credit to vakho10 on github for this key logger implementation
 * https://github.com/vakho10/java-keylogger/blob/master/src/main/java/ge/edu/sangu/KeyLogger.java
 */
@Singleton
public class GlobalInputEventService implements NativeKeyListener {
    
    // Buffer with capacity of 25 (Max QR string size is 22)
    private final LinkedBlockingDeque<Character> keyCharBuffer = new LinkedBlockingDeque<>(25);
    private final String QR_CODE_HEADER =  "sta";

    @Inject
    public GlobalInputEventService() {
        try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
            System.out.println("Keylogger failed to register");
			e.printStackTrace();
            return; // This feels really wrong
		}
		GlobalScreen.addNativeKeyListener(this);
    }

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
        if (e.getKeyCode() != NativeKeyEvent.VC_ENTER) {
            return;
        }
        String bufferStr = keyCharBuffer.stream() // converts buffer to a string
            .map(String::valueOf)
            .collect(Collectors.joining())
            .toLowerCase();
        // String qrCode = QRParsingUtils.verifyQRString(QR_CODE_HEADER, bufferStr);
        // if (qrCode == null) {
        //     return;
        // }
        // attendanceService.startOngoingVisit(null);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent keyEvent) {
    }

}
