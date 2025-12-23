package io.github.unchangingconstant.studenttracker.app.mappers.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import io.github.unchangingconstant.studenttracker.app.domain.OngoingVisitDomain;
import io.github.unchangingconstant.studenttracker.gui.models.OngoingVisitModel;

public class DomainToOngoingVisitModelMapper {
    
    public static OngoingVisitModel map(OngoingVisitDomain domain)   {
        return new OngoingVisitModel(
            domain.getStudentId(), 
            domain.getStartTime(), 
            (domain.getSubjects() * 30) - ChronoUnit.MINUTES.between(domain.getStartTime(), Instant.now()),
            domain.getSubjects(),
            domain.getStudentName());
    }

}
