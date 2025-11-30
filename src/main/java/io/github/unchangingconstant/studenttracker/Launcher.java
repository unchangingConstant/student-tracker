package io.github.unchangingconstant.studenttracker;

public class Launcher {

    // This should somehow avoid java runtime components missing error when
    // packaging? (It does, somehow)
    public static void main(String[] args) throws Exception {
        ApplicationSetup.setUp();
        StudentTrackerApp.launch(StudentTrackerApp.class, args);
    }

}
