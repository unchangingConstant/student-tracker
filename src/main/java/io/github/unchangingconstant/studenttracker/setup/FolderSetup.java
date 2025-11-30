package io.github.unchangingconstant.studenttracker.setup;

import java.io.File;

public class FolderSetup {
    
    public static void setupFolders() throws Exception {
        File exportsPath = new File(System.getProperty("user.dir") + File.separator + "exports");
        if (exportsPath.exists()) return;
        Boolean dirCreated = exportsPath.mkdir();
        if (!dirCreated) {
            throw new Exception("Record folder creation failed");
        }
    }

}
