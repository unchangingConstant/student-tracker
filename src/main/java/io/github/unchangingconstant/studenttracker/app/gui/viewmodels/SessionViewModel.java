package io.github.unchangingconstant.studenttracker.app.gui.viewmodels;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.OngoingVisit;
import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.backend.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.gui.models.OngoingVisitTableModel;
import io.github.unchangingconstant.studenttracker.app.gui.models.StudentTableModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import lombok.Getter;

public class SessionViewModel {

    private OngoingVisitTableModel ongoingVisitsModel;
    private StudentTableModel studentsModel;
    private AttendanceService service;

    @Getter
    private SimpleStringProperty studentSelectorInput = new SimpleStringProperty();
    @Getter
    private SimpleObjectProperty<Student> selected = new SimpleObjectProperty<>(null);

    // Corresponds update-second to ongoing visits for efficient updating
    @Inject
    public SessionViewModel(OngoingVisitTableModel ongoingVisitsModel, StudentTableModel studentsModel, AttendanceService service) {
        this.ongoingVisitsModel = ongoingVisitsModel;
        this.studentsModel = studentsModel;
        this.service = service;
    }

    public void bindToOngoingVisitsModel(Property<ObservableList<OngoingVisit>> prop) {
        ongoingVisitsModel.bind(prop);
    }

    public void bindToStudentsModel(Property<ObservableList<Student>> prop) {
        studentsModel.bind(prop);
    }

    public void onStartVisitAction() {
        service.startOngoingVisit(selected.getValue().getStudentId());
        studentSelectorInput.set("");
    }
}
