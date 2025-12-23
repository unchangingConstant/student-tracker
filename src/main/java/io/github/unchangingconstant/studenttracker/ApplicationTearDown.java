package io.github.unchangingconstant.studenttracker;

import io.github.unchangingconstant.studenttracker.threads.ThreadManager;

public class ApplicationTearDown {
    
    public static void tearDown() {
        ThreadManager.closeThreads();
    }

}
