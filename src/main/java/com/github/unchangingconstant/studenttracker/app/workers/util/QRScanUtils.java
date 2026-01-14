package com.github.unchangingconstant.studenttracker.app.workers.util;

import java.util.concurrent.LinkedBlockingDeque;

public class QRScanUtils {
    
    public static void addToKeyBuffer(LinkedBlockingDeque<Character> buffer, Character keyChar) {
        if (!buffer.offer(keyChar)) {
            buffer.removeFirst();
            buffer.add(keyChar);
        }
    }

}
