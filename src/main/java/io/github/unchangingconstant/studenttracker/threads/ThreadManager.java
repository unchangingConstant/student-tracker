package io.github.unchangingconstant.studenttracker.threads;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Will manage the lifecycle of backbround threads and provide an interface for the 
 * application to utilize them.
 * 
 * It will NOT manage the JavaFX thread. But the JavaFX thread might call it to 
 * execute background tasks
 */
public class ThreadManager {

    // For most tasks, a single synchronized background thread will suffice
    private static final ExecutorService mainBackgroundThread = Executors.newSingleThreadExecutor(); 
    // List of threads to close upon application exit
    private static final CopyOnWriteArrayList<Thread> managedExternalThreads = new CopyOnWriteArrayList<>();

    private ThreadManager() {
    }

    public static ExecutorService mainBackgroundExecutor() {
        return mainBackgroundThread;
    }

    // Given a thread, will ensure it closes upon app exit
    // This is solely because jnativehook offers no way to easily run on a user-made thread
    public static void manageExternalThread(Thread externalThread) {
        managedExternalThreads.add(externalThread);
    }

    public static void closeThreads() {
        /**
         * TODO the main background thread will mostly only do small tasks, and will
         * seldom take long to finish them. Does it require any cleanup? Figure this out.
         */
        managedExternalThreads.forEach(thread -> thread.interrupt());
        mainBackgroundThread.close();
    }

}
