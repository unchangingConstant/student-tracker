package com.github.unchangingconstant.studenttracker;

import java.io.File;

import org.jdbi.v3.core.Jdbi;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.unchangingconstant.studenttracker.utils.ResourceLoader;

public class ApplicationSetup {

    public static void setUp() throws Exception {
        setupFolders();
        setupDatabase();
        setupKeyLogger();
    }

    // TODO OMG READ section 1.14.3 OF JDBI DOCS!!!!
    private static void setupDatabase() {
        Jdbi jdbi = Jdbi.create("jdbc:sqlite:database.db");
        String studentTable = ResourceLoader.loadResource("/sql/schema/studentTable.sql");
        String visitTable = ResourceLoader.loadResource("/sql/schema/visitTable.sql");
        String ongoingVisitTable = ResourceLoader.loadResource("/sql/schema/ongoingVisitTable.sql");
        jdbi.withHandle(handle -> handle.execute(studentTable));
        jdbi.withHandle(handle -> handle.execute(visitTable));
        jdbi.withHandle(handle -> handle.execute(ongoingVisitTable));
    }

    private static void setupFolders() throws Exception {
        File exportsPath = new File(System.getProperty("user.dir") + File.separator + "exports");
        if (exportsPath.exists()) return;
        Boolean dirCreated = exportsPath.mkdir();
        if (!dirCreated) {
            throw new Exception("Record folder creation failed");
        }
    }

    private static void setupKeyLogger() {
        try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}
    }

}
