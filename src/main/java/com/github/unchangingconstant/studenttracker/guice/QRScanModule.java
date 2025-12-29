package com.github.unchangingconstant.studenttracker.guice;

import com.github.unchangingconstant.studenttracker.app.workers.QRScanWorker;
import com.google.inject.AbstractModule;

public class QRScanModule extends AbstractModule {

    @Override
    public void configure() {
        bind(QRScanWorker.class).asEagerSingleton();
    }

}
