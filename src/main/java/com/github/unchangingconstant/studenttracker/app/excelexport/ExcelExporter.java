package com.github.unchangingconstant.studenttracker.app.excelexport;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.time.Duration;

import com.github.unchangingconstant.studenttracker.app.entities.Student;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import com.github.unchangingconstant.studenttracker.app.dbmanager.DatabaseDAO;
import com.github.unchangingconstant.studenttracker.app.entities.Visit;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import lombok.Builder;
import lombok.Data;

import static com.github.unchangingconstant.studenttracker.app.excelexport.util.ExcelExportUtils.*;

@Singleton
public class ExcelExporter {

    private final DatabaseDAO dao;

    private static final Comparator<VisitExport> SORT_BY_TIME =
        new Comparator<VisitExport>() {
            @Override
            public int compare(VisitExport arg0, VisitExport arg1) {
                return arg1.getStartTime().compareTo(arg0.getStartTime());
            }
        };
    private static final Comparator<VisitExport> SORT_BY_NAME =
        new Comparator<VisitExport>() {
            @Override
            public int compare(VisitExport arg0, VisitExport arg1) {
                if (arg0.getStudentName().equals(arg1.getStudentName())) {
                    return SORT_BY_TIME.compare(arg0, arg1);
                }
                return arg0.getStudentName().compareTo(arg1.getStudentName());
            }
        };

    @Inject
    public ExcelExporter(DatabaseDAO dao) {
        this.dao = dao;
    }

    public String exportStudentsVisitsToExcel(List<Integer> studentIds) throws Exception {
        // TODO add a database method to do this in one call (Rework database perhaps? Getting kind of monolithic)
        List<Visit> studentsVisits = dao.getMultipleStudentsVisits(studentIds);
        // Creates a map with studentIds as keys and student names as values
        Map<Integer, String> studentNames = dao.findStudentsWithId(studentIds).values().stream()
            .collect(Collectors.toMap(Student::getStudentId, Student::getFullName));

        List<VisitExport> exportedVisits = studentsVisits.stream()
            .map(visit -> {
                return VisitExport.builder()
                    .studentName(studentNames.get(visit.getStudentId()))
                    .startTime(visit.getStartTime())
                    .endTime(visit.getStartTime().plus(Duration.ofMinutes(visit.getDuration())))
                    .duration(visit.getDuration())
                    .build();
            }).sorted(SORT_BY_NAME).toList();

        // Creates file name
        String exportName = EXCEL_EXPORT_PATH + generateExcelExportName();

        // Writes data to excel workbook
        // TODO first example of versioning perhaps being necessary?
        try (OutputStream os = Files.newOutputStream(Paths.get(exportName));
             Workbook wb = new Workbook(os, "StudentTracker", "0.1")
        ) {
            Worksheet ws = wb.newWorksheet("StudentVisits");
            // Create field headers
            ws.value(0, 0, "Student Name");
            ws.value(0, 1, "Start Time");
            ws.value(0, 2, "End Time");
            ws.value(0, 3, "Duration (Minutes)");
            // Populate sheet with data
            for (int row = 1; row < exportedVisits.size() + 1; row++) {
                VisitExport currExport = exportedVisits.get(row - 1);
                Instant startTime = currExport.getStartTime();
                Instant endTime = currExport.getEndTime();
                ws.value(row, 0, currExport.getStudentName());
                ws.value(row, 1, generateDateTimeCellStr(startTime));
                ws.value(row, 2, generateDateTimeCellStr(endTime));
                ws.value(row, 3, ChronoUnit.MINUTES.between(startTime, endTime));
            }
        }

        return exportName;
    }

    @Data
    @Builder
    private static class VisitExport {
        private String studentName;
        private Instant startTime;
        private Instant endTime;
        private Integer duration;
    }
}
