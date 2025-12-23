package io.github.unchangingconstant.studenttracker.app.mappers.model;

import io.github.unchangingconstant.studenttracker.app.domain.StudentDomain;
import io.github.unchangingconstant.studenttracker.gui.models.StudentModel;

public class DomainToStudentModelMapper {
    
    public static StudentModel map(StudentDomain domain)   {
        return new StudentModel(
            domain.getStudentId(), 
            domain.getFullLegalName(), 
            domain.getPrefName(), 
            domain.getDateAdded(), 
            domain.getSubjects());
    }

}
