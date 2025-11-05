package io.github.unchangingconstant.studenttracker.app.gui.viewmodels;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.OngoingVisit;
import io.github.unchangingconstant.studenttracker.app.backend.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.gui.models.OngoingVisitTableModel;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;


public class SessionViewModel {

    private OngoingVisitTableModel model;

    // Corresponds update-second to ongoing visits for efficient updating
    @Inject
    public SessionViewModel(OngoingVisitTableModel model, AttendanceService service) {
        this.model = model;
    }

    public void bindToModelProperty(Property<ObservableList<OngoingVisit>> prop) {
        model.bind(prop);
    }
}
