package io.github.unchangingconstant.studenttracker.app.viewmodels;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.domain.OngoingVisitDomain;
import io.github.unchangingconstant.studenttracker.app.domain.StudentDomain;
import io.github.unchangingconstant.studenttracker.app.models.OngoingVisitTableModel;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import io.github.unchangingconstant.studenttracker.app.models.StudentTableModel;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import lombok.Getter;

public class AttendanceDashboardViewModel {

    private OngoingVisitTableModel ongoingVisitsModel;
    private StudentTableModel studentsModel;
    private AttendanceService service;

    @Getter
    private SimpleStringProperty studentSelectorInput = new SimpleStringProperty();
    @Getter
    private SimpleObjectProperty<StudentModel> selected = new SimpleObjectProperty<>(null);

    // Corresponds update-second to ongoing visits for efficient updating
    @Inject
    public AttendanceDashboardViewModel(OngoingVisitTableModel ongoingVisitsModel, StudentTableModel studentsModel, AttendanceService service) {
        this.ongoingVisitsModel = ongoingVisitsModel;
        this.studentsModel = studentsModel;
        this.service = service;
    }

    public void bindToOngoingVisitsModel(Property<ObservableList<OngoingVisitDomain>> prop) {
        ongoingVisitsModel.bind(prop);
    }

    public void bindToStudentsModel(Property<ObservableList<StudentModel>> prop) {
        studentsModel.bind(prop);
    }

    public void onStartVisitAction() {
        service.startOngoingVisit(selected.getValue().getStudentId().get());
        studentSelectorInput.set("");
    }

    public void onEndOngoingVisit(Integer studentId) {
        service.endOngoingVisit(ongoingVisitsModel.get(studentId));
    }
}
