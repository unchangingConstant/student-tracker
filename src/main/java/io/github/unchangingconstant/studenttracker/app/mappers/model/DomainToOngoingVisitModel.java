package io.github.unchangingconstant.studenttracker.app.mappers.model;

import io.github.unchangingconstant.studenttracker.app.domain.OngoingVisitDomain;
import io.github.unchangingconstant.studenttracker.app.models.OngoingVisitModel;

public class DomainToOngoingVisitModel {
    
    public static OngoingVisitModel map(OngoingVisitDomain domain)   {
        return new OngoingVisitModel(
            domain.getStudentId(), 
            domain.getStartTime(), 
            domain.getStudentName());
    }

}
