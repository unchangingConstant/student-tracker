package io.github.unchangingconstant.studenttracker.app.viewmodels;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.entities.Visit;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;

public class OngoingVisitsViewModel {

    private Property<ObservableList<Visit>> ongoingVisits;

    public OngoingVisitsViewModel() {

    }

}
