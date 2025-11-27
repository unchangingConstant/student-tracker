package io.github.unchangingconstant.studenttracker.app.services;

import java.io.FileWriter;
import java.io.Writer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import io.github.unchangingconstant.studenttracker.StudentTrackerApp;
import io.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;
import io.github.unchangingconstant.studenttracker.app.domain.VisitDomain;

public class ExportCSVService {
    
    // TODO How are you gonna mock the DAO? Will you at all? How are you going to handle stateless services?
    public static void exportStudentVisitsCSV(Integer studentId, String filePath) {
        DatabaseDAO dao = StudentTrackerApp.appContext.getInstance(DatabaseDAO.class);
        List<VisitDomain> visits = dao.getStudentVisits(studentId);
        String[] headers = new String[]{"StartDate", "StartTime", "Duration"};

        try {
            Writer writer = new FileWriter(filePath + Instant.now().toString());
            CSVPrinter csv = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy").withZone(ZoneId.systemDefault());
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
            for (VisitDomain visit: visits) {
                csv.printRecord(
                    dateFormatter.format(visit.getStartTime()), 
                    timeFormatter.format(visit.getStartTime()), 
                    String.valueOf(ChronoUnit.MINUTES.between(visit.getStartTime(), visit.getEndTime())));
            }
            csv.close();
		} catch (Exception e) {
            e.printStackTrace();
        }
    }
}
