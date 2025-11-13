package io.github.unchangingconstant.studenttracker.app.mappers.model;

import io.github.unchangingconstant.studenttracker.app.domain.StudentDomain;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;

public class DomainToStudentModelMapper {
    
    public static StudentModel map(StudentDomain domain)   {
        return new StudentModel(
            domain.getStudentId(), 
            domain.getFirstName(), 
            domain.getMiddleName(), 
            domain.getLastName(), 
            domain.getDateAdded(), 
            domain.getSubjects());
    }

}
