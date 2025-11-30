package io.github.unchangingconstant.studenttracker;

import io.github.unchangingconstant.studenttracker.setup.DatabaseSetup;
import io.github.unchangingconstant.studenttracker.setup.FolderSetup;

public class ApplicationSetup {

    public static void setUp() throws Exception {
        FolderSetup.setupFolders();
        DatabaseSetup.setUpDatabase();
    }

}
