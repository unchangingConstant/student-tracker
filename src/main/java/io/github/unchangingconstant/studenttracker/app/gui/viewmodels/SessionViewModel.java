package io.github.unchangingconstant.studenttracker.app.gui.viewmodels;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Visit;
import io.github.unchangingconstant.studenttracker.app.backend.services.VisitService;
import io.github.unchangingconstant.studenttracker.app.gui.models.OngoingVisitTableModel;
import io.github.unchangingconstant.studenttracker.app.gui.viewmodels.bindings.ListToMapBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.ObservableList;

public class SessionViewModel {

    private OngoingVisitTableModel model;
    private SimpleListProperty<Visit> modelProp;
    private ListToMapBinding<Visit, Integer, Integer> updateQueue;
    private ListToMapBinding<Visit, Integer, SimpleLongProperty> timeRemainingRef;

    @Inject
    public SessionViewModel(OngoingVisitTableModel model, VisitService service) {
        this.model = model;
        this.modelProp = new SimpleListProperty<>();
        model.bind(modelProp);
        bindTimeRemainingRefToModel();
        bindUpdateQueueToModel();
    }

    public void bindToTimeRemainingProperty(SimpleLongProperty prop, Integer visitId) {
    }

    public void bindToModelProperty(Property<ObservableList<Visit>> prop) {
        model.bind(prop);
    }

    private void updateRemainingTime() {
    }

    private void bindTimeRemainingRefToModel() {
        this.timeRemainingRef = new ListToMapBinding<Visit, Integer, SimpleLongProperty>(modelProp.get()) {

            @Override
            protected Integer keyFactory(Visit item) {
                return item.getVisitId();
            }

            @Override
            protected SimpleLongProperty valueFactory(Visit item) {
                return new SimpleLongProperty(ChronoUnit.MINUTES.between(LocalDateTime.now(),
                        item.getStartTime()));
            }
        };
    }

    private void bindUpdateQueueToModel() {
        this.updateQueue = new ListToMapBinding<Visit, Integer, Integer>(modelProp.get()) {

            @Override
            protected Integer keyFactory(Visit item) {
                return item.getStartTime().getSecond();
            }

            @Override
            protected Integer valueFactory(Visit item) {
                return item.getVisitId();
            }
        };
    }
}
