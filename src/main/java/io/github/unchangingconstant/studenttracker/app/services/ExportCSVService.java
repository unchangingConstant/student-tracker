package io.github.unchangingconstant.studenttracker.app.services;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.domain.VisitDomain;

@Singleton
public class ExportCSVService {
    
    AttendanceService service;

    @Inject
    public ExportCSVService(AttendanceService attendanceService) {
        this.service = attendanceService;
    }

    // TODO do you seriously need a library to export CSVs? Maybe to export an xlsx, yes. 
    // Write the CSV code yourself, pussy
    public void exportStudentVisitsCSV(Integer studentId) {      
        List<VisitDomain> visits = service.getStudentVisits(studentId);
        String[] headers = new String[]{"StartDate", "StartTime", "Duration"};

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy").withZone(ZoneId.systemDefault());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());
        Instant now = Instant.now();

        String exportName = 
            "." + File.separator +
            "exports" + File.separator + 
            service.getStudent(studentId).getFullLegalName().replaceAll("\\s", "") + "_" +
            dateFormatter.format(now) + "_" + timeFormatter.format(now) + ".csv";

        try {
            Writer writer = new FileWriter(exportName);
            CSVPrinter csv = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers));
            timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
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
