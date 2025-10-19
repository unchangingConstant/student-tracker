package io.github.unchangingconstant.studenttracker.app.viewmodels;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.entities.domain.Visit;
import io.github.unchangingconstant.studenttracker.app.models.OngoingVisitsModel;
import io.github.unchangingconstant.studenttracker.app.services.VisitService;
import javafx.beans.property.Property;
import javafx.collections.ObservableMap;

public class OngoingVisitsViewModel {

    private Property<ObservableMap<Integer, Visit>> ongoingVisits;

    private VisitService dbAccess;

    @Inject
    public OngoingVisitsViewModel(OngoingVisitsModel model, VisitService dbAccess) {
        this.dbAccess = dbAccess;
        // TODO make dis bruh
    }

    public void onEndVisit(Integer visitId) {
        dbAccess.endVisit(visitId);
    }

}
