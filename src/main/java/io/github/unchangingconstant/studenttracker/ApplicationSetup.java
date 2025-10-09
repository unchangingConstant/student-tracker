package io.github.unchangingconstant.studenttracker;

import io.github.unchangingconstant.studenttracker.setup.DatabaseSetup;

public class ApplicationSetup {

    public static void setUp() {
        DatabaseSetup.setUpDatabase();
    }

}
