package com.github.unchangingconstant.studenttracker.app.services.util;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ExcelExportUtils {

    /*
     * Using the same formatters for file names and excel export formatters seems
     * wrong (Especially since characters that are valid for an excel sheet aren't
     * always valid for file names, i.e. a colon in Windows).
     * Consider another way.
     */
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
            .withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    public static final String EXCEL_EXPORT_PATH = "." + File.separator + "exports" + File.separator;

    public static String generateExcelExportName() {
        Instant now = Instant.now();
        return sanitizeFilename(dateFormatter.format(now) + "_" + timeFormatter.format(now) + ".xlsx");
    }

    public static String generateDateTimeCellStr(Instant time) {
        return dateFormatter.format(time) + "_" + timeFormatter.format(time);
    }

    // All files names will be run through this
    // Ensures filenames do not conflict with operating system naming restrictions
    // Shout out Claude
    private static String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9._\\-\\s]", "_");
    }

}
