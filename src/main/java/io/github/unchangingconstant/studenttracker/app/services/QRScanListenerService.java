package io.github.unchangingconstant.studenttracker.app.services;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * QRCodes will represent studentIds via two, 6-digit hex-numbers representing its studentId. 
 * The scanner will check that the two numbers are the same to prevent scanner mistakes. 
 * 
 * An example of a QRCode string will look like this:
 * 
 * STA00001A00001A
 * 
 * or (STA + 00001A + 00001A)
 * 
 * This would be a studentId of 26, represented in hex.
 *
 * Credit to vakho10 on github for this key logger implementation
 * https://github.com/vakho10/java-keylogger/blob/master/src/main/java/ge/edu/sangu/KeyLogger.java
 */
@Singleton
public class QRScanListenerService implements NativeKeyListener {
    
    // TODO sooo like u want to use char arrays or Strings? Make up your damn mind
    private final CharacterBuffer buffer = new CharacterBuffer(15);
    private final Character[] QR_CODE_HEADER =  new Character[] {'S', 'T', 'A'};

    private AttendanceService attendanceService;

    @Inject
    public QRScanListenerService(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
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
        Character keyText = e.getKeyChar();
        buffer.add(keyText);

        Character[] currHeader = buffer.subArray(0, 3);

        if (Arrays.equals(currHeader, QR_CODE_HEADER)) {
            Integer studentId = validateQRCode(buffer.subArray(3, 15));
            if (studentId == null) {
                throw new RuntimeException("QRCode invalid. IMPLEMENT BETTER EXCEPTION HANDLING AND WRITE YOUR GODDAMN TESTS!");
            }
            attendanceService.startOngoingVisit(studentId);
        }
    }

    // If QRCode valid, returns decoded studentId. Returns null if code invalid
    private Integer validateQRCode(Character[] qrCode) {
        String qrStr = Arrays.stream(qrCode)
            .map(String::valueOf)
            .collect(Collectors.joining());

        Integer firstHexId = Integer.parseInt(qrStr.substring(0, 6), 16);
        Integer secondHexId = Integer.parseInt(qrStr.substring(6, 12), 16);

        if (firstHexId == secondHexId) {
            return firstHexId;
        }
        return null;
    }

    class CharacterBuffer {

        private final List<Character> buffer;
        private final Integer maxLength;

        private CharacterBuffer(Integer maxLength) {
            this.maxLength = maxLength;
            this.buffer = new ArrayList<>(maxLength);
        }

        private void add(Character newChar) {
            if (buffer.size() == maxLength) {
                buffer.removeLast();
            }
            buffer.addFirst(newChar);
        }

        private Character[] subArray(Integer startIndex, Integer lastIndex) {
            return buffer.subList(startIndex, lastIndex).toArray(new Character[lastIndex - startIndex]);
        }

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent keyEvent) {
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent keyEvent) {
    }

}
