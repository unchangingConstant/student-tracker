package com.github.unchangingconstant.studenttracker.app.mappers.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.github.unchangingconstant.studenttracker.app.domain.OngoingVisit;
import com.github.unchangingconstant.studenttracker.gui.models.OngoingVisitModel;

public class DomainToOngoingVisitModelMapper {
    
    public static OngoingVisitModel map(OngoingVisit domain)   {
        return new OngoingVisitModel(
            domain.getStudentId(), 
            domain.getStartTime(), 
            (domain.getSubjects() * 30) - ChronoUnit.MINUTES.between(domain.getStartTime(), Instant.now()),
            domain.getSubjects(),
            domain.getStudentName());
    }

}
