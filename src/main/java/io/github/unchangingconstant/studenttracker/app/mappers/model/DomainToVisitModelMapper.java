package io.github.unchangingconstant.studenttracker.app.mappers.model;

import io.github.unchangingconstant.studenttracker.app.domain.VisitDomain;
import io.github.unchangingconstant.studenttracker.app.models.VisitModel;

public class DomainToVisitModelMapper {
    
    public static VisitModel map(VisitDomain domain) {
        return new VisitModel(
            domain.getVisitId(), 
            domain.getStartTime(), 
            domain.getEndTime(), 
            domain.getStudentId());
    }
}
