package com.github.unchangingconstant.studenttracker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

public class ApplicationTearDown {
    
    public static void tearDown() {
        // Stops global keylogger
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }
}
