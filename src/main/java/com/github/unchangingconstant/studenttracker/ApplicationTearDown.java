package com.github.unchangingconstant.studenttracker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.unchangingconstant.studenttracker.threads.ThreadManager;

public class ApplicationTearDown {
    
    public static void tearDown() {
        ThreadManager.closeThreads();
        // Stops global keylogger
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }

    

}
