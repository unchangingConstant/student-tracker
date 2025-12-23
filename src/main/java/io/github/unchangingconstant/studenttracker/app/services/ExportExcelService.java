package io.github.unchangingconstant.studenttracker.app.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;
import io.github.unchangingconstant.studenttracker.app.domain.VisitDomain;

@Singleton
public class ExportExcelService {
    
    private DatabaseDAO dao;

    @Inject
    public ExportExcelService(DatabaseDAO dao) {
        this.dao = dao;
    }

    // TODO create database method to retrieve visits from multiple students
    public synchronized void exportStudentsVisitsToExcel(List<Integer> studentIds) throws Exception {
        // TODO add a database method to do this in one call (Rework database perhaps? Getting kind of monolithic)
        List<VisitDomain> studentsVisits = dao.getMultipleStudentsVisits(studentIds);
        // Creates a map with studentIds as keys and student names as values
        Map<Integer, String> studentNames = dao.getStudents(studentIds).stream()
            .collect(Collectors.toMap(
                student -> student.getStudentId(),
                student -> student.getFullLegalName() 
            ));

        // Creates file name
        Instant now = Instant.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy").withZone(ZoneId.systemDefault());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());
        String exportName = 
            "." + File.separator +
            "exports" + File.separator + 
            dateFormatter.format(now) + "_" + timeFormatter.format(now) + ".xlsx";

        // Writes data to excel workbook
        // TODO first example of versioning perhaps being necessary?
        try (OutputStream os = Files.newOutputStream(Paths.get(exportName)); Workbook wb = new Workbook(os, "StudentTracker", "0.1")) {
            Worksheet ws = wb.newWorksheet("StudentVisits");
            // Create field headers
            ws.value(0, 0, "Student Name");
            ws.value(0, 1, "Start Time");
            ws.value(0, 2, "End Time");
            ws.value(0, 3, "Duration (Minutes)");
            // Populate sheet with data
            for (int row = 1; row < studentsVisits.size() + 1; row++) {
                VisitDomain currVisit = studentsVisits.get(row - 1);
                Instant startTime = currVisit.getStartTime();
                Instant endTime = currVisit.getEndTime();
                ws.value(row, 0, studentNames.get(currVisit.getStudentId()));
                ws.value(row, 1, dateFormatter.format(startTime) + "_" + timeFormatter.format(startTime));
                ws.value(row, 2, dateFormatter.format(endTime) + "_" + timeFormatter.format(endTime));
                ws.value(row, 3, ChronoUnit.MINUTES.between(startTime, endTime));
            }
        }
    }
}
