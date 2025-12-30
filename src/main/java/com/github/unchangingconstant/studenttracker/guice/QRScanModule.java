package com.github.unchangingconstant.studenttracker.guice;

import com.github.unchangingconstant.studenttracker.app.workers.QRScanWorker;
import com.google.inject.AbstractModule;

public class QRScanModule extends AbstractModule {

    @Override
    public void configure() {
        // This class won't be created by the app entry point
        // So, we gotta do it manually
        // Eager singleton - singleton that's created as soon as the injector is created
        bind(QRScanWorker.class).asEagerSingleton();
    }

}
