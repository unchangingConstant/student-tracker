package com.github.unchangingconstant.studenttracker.app.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import com.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;
import com.github.unchangingconstant.studenttracker.app.domain.ExportedVisitDomain;
import com.github.unchangingconstant.studenttracker.app.domain.VisitDomain;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ExportExcelService {
    
    private DatabaseDAO dao;

    public static final Comparator<ExportedVisitDomain> SORT_BY_TIME = 
        new Comparator<ExportedVisitDomain>() {
            @Override
            public int compare(ExportedVisitDomain arg0, ExportedVisitDomain arg1) {
                return arg1.getStartTime().compareTo(arg0.getStartTime());
            }
        };
    public static final Comparator<ExportedVisitDomain> SORT_BY_NAME = 
        new Comparator<ExportedVisitDomain>() {
            @Override
            public int compare(ExportedVisitDomain arg0, ExportedVisitDomain arg1) {
                if (arg0.getStudentName().equals(arg1.getStudentName())) {
                    return SORT_BY_TIME.compare(arg0, arg1);
                }
                return arg0.getStudentName().compareTo(arg1.getStudentName());
            }
        };

    @Inject
    public ExportExcelService(DatabaseDAO dao) {
        this.dao = dao;
    }

    // TODO create database method to retrieve visits from multiple students
    public String exportStudentsVisitsToExcel(List<Integer> studentIds, Comparator<ExportedVisitDomain> comparator) throws Exception {
        // TODO add a database method to do this in one call (Rework database perhaps? Getting kind of monolithic)
        List<VisitDomain> studentsVisits = dao.getMultipleStudentsVisits(studentIds);
        // Creates a map with studentIds as keys and student names as values
        Map<Integer, String> studentNames = dao.getStudents(studentIds).stream()
            .collect(Collectors.toMap(
                student -> student.getStudentId(),
                student -> student.getFullLegalName() 
            ));

        List<ExportedVisitDomain> exportedVisits = 
            studentsVisits.stream()
            .map(visit -> {
                return ExportedVisitDomain.builder()
                .studentName(studentNames.get(visit.getStudentId()))
                .startTime(visit.getStartTime())
                .endTime(visit.getEndTime())
                .duration(ChronoUnit.MINUTES.between(visit.getStartTime(), visit.getEndTime()))
                .build();
            }).collect(Collectors.toList());
        
        if (comparator != null) Collections.sort(exportedVisits, comparator);

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
            for (int row = 1; row < exportedVisits.size() + 1; row++) {
                ExportedVisitDomain currExport = exportedVisits.get(row - 1);
                Instant startTime = currExport.getStartTime();
                Instant endTime = currExport.getEndTime();
                ws.value(row, 0, currExport.getStudentName());
                ws.value(row, 1, dateFormatter.format(startTime) + "_" + timeFormatter.format(startTime));
                ws.value(row, 2, dateFormatter.format(endTime) + "_" + timeFormatter.format(endTime));
                ws.value(row, 3, ChronoUnit.MINUTES.between(startTime, endTime));
            }
        }

        return exportName;
    }

    private List<ExportedVisitDomain> sortExports(List<ExportedVisitDomain> exportList, Comparator<ExportedVisitDomain> comparator) {
        return null;
    }
}
