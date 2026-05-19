package com.github.unchangingconstant.studenttracker.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadsModule extends AbstractModule {

    @Provides
    @Singleton
    public Executor provideSingleThreadExecutor() {
        return Executors.newSingleThreadExecutor(
            new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull Runnable runnable) {
                    Thread thread = new Thread(runnable);
                    // Ensures thread doesn't keep running once GUI is closed
                    thread.setDaemon(true);
                    return thread;
                }
            });
    }

}
