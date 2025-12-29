package io.github.unchangingconstant.studenttracker.guice;

import com.google.inject.AbstractModule;

import io.github.unchangingconstant.studenttracker.app.workers.QRScanWorker;

public class QRScanModule extends AbstractModule {

    @Override
    public void configure() {
        bind(QRScanWorker.class).asEagerSingleton();
    }

}
