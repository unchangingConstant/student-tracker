package com.github.unchangingconstant.studenttracker.app.mappers.model;

import com.github.unchangingconstant.studenttracker.app.domain.Visit;
import com.github.unchangingconstant.studenttracker.gui.models.VisitModel;

public class DomainToVisitModelMapper {
    
    public static VisitModel map(Visit domain) {
        return new VisitModel(
            domain.getVisitId(), 
            domain.getStartTime(), 
            domain.getEndTime(), 
            domain.getStudentId());
    }
}
