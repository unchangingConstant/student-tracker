package io.github.unchangingconstant.studenttracker.app.models;

import java.util.Collection;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.domain.StudentDomain;
import io.github.unchangingconstant.studenttracker.app.mappers.model.DomainToStudentModelMapper;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.services.Observer;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Singleton
public class VisitTableModel {
    
    private final AttendanceService attendanceService;

    private final SimpleListProperty<VisitModel> visits;  
    
    private final SimpleIntegerProperty currentStudent;

    @Inject
    public VisitTableModel(AttendanceService attendanceService) {
        this.currentStudent = new SimpleIntegerProperty(-1);
        this.visits = new SimpleListProperty<>();
        this.attendanceService = attendanceService;
    }

    public void bind(Property<ObservableList<VisitModel>> prop) {
        prop.bind(visits);
    }

    private void setupCurrentStudentProperty() {
        currentStudent.addListener((obs, oldVal, newVal) -> {
            
        });
    }

}
