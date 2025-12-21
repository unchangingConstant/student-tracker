package io.github.unchangingconstant.studenttracker.app.services;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.grapecity.documents.excel.*;   
import com.grapecity.documents.excel.drawing.*;
import io.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;
import io.github.unchangingconstant.studenttracker.app.domain.VisitDomain;
import io.github.unchangingconstant.studenttracker.app.domain.StudentDomain;

@Singleton
public class ExportExcelService {
    
    private DatabaseDAO dao;

    @Inject
    public ExportExcelService(DatabaseDAO dao) {
        this.dao = dao;
    }

    // TODO create database method to retrieve visits from multiple students
    public void exportStudentsVisitsToExcel(List<Integer> studentIds) {
        // TODO add a database method to do this in one call (Rework database perhaps? Getting kind of monolithic)
        List<VisitDomain> studentsVisits = dao.getMultipleStudentsVisits(studentIds);
        // Creates a map with studentIds as keys and student names as values
        Map<Integer, String> studentNames = dao.getStudents(studentIds).stream()
            .collect(Collectors.toMap(
                student -> student.getStudentId(),
                student -> student.getFullLegalName() 
            ));

        // Creates 2D table of all visit info to be exported
        Object[][] dataset = new Object[studentsVisits.size()][4];

        for (int row = 0; row < dataset.length; row++) {
            VisitDomain currVisit = studentsVisits.get(row);
            dataset[row] = new Object[] {
                studentNames.get(currVisit.getStudentId()),
                currVisit.getStartTime(),
                currVisit.getEndTime(),
                ChronoUnit.MINUTES.between(currVisit.getStartTime(), currVisit.getEndTime())
            };
        }

        // Writes data to excel workbook
        Workbook workbook = new Workbook();
        IWorksheet worksheet = workbook.getWorksheets().get(0);
        worksheet.getRange("A1:" + mapRowCountToExcelRow(dataset.length) + String.valueOf(dataset[0].length)).setValue(dataset);

        // Creates file name
        Instant now = Instant.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy").withZone(ZoneId.systemDefault());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());
        String exportName = 
            "." + File.separator +
            "exports" + File.separator + 
            dateFormatter.format(now) + "_" + timeFormatter.format(now) + ".xlsx";

        // Saves excel file
        workbook.save(exportName);
    }

    private String mapRowCountToExcelRow(Integer rows) {
        String excelRow = "";
        if (rows / 26 > 0) {
            excelRow = excelRow + mapRowCountToExcelRow(rows / 26);
        }
        excelRow = excelRow + (char)((rows % 26) + 65);
        return excelRow;
    }
}
