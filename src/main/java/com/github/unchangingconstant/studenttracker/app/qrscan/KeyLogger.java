package com.github.unchangingconstant.studenttracker.app.qrscan;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.unchangingconstant.studenttracker.app.qrscan.QRScanner.KeyEventHook;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class KeyLogger {
    
    @Inject
    public KeyLogger() {
    }

    public void addNativeKeyListener(KeyEventHook keyEventHook) {
        GlobalScreen.addNativeKeyListener(keyEventHook);
    }

}
