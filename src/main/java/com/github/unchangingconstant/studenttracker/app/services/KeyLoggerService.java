package com.github.unchangingconstant.studenttracker.app.services;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.unchangingconstant.studenttracker.app.workers.QRScanWorker.KeyEventHook;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class KeyLoggerService {
    
    @Inject
    public KeyLoggerService() {
    }

    public void addNativeKeyListener(KeyEventHook keyEventHook) {
        GlobalScreen.addNativeKeyListener(keyEventHook);
    }

}
