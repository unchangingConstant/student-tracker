package io.github.unchangingconstant.studenttracker.app;

public class Launcher {

    // This should somehow avoid java runtime components missing error when
    // packaging? (It does, somehow)
    public static void main(String[] args) {
        StudentTrackerApp.launch(StudentTrackerApp.class, args);
    }

}
