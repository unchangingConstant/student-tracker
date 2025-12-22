package io.github.unchangingconstant.studenttracker.config;

import com.google.inject.AbstractModule;

import io.github.unchangingconstant.studenttracker.app.services.QRScanListenerService;

public class QRScanListenerModule extends AbstractModule {
    
    @Override
    public void configure() {
        bind(QRScanListenerService.class).asEagerSingleton();
    }

}
