package io.github.unchangingconstant.studenttracker.app.services;

import java.time.LocalDateTime;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;
import io.github.unchangingconstant.studenttracker.app.entities.Visit;

@Singleton
public class VisitsTableService {

    private DatabaseDAO dao;
    private VisitsTableEventService eventService;

    @Inject
    public VisitsTableService(DatabaseDAO dao, VisitsTableEventService eventService) {
        this.eventService = eventService;
        this.dao = dao;
    }

    public Integer startVisit(Integer studentId) {
        // Must not have ongoing visits when starting one
        Integer result = this.dao.insertVisit(LocalDateTime.now(), null, studentId);
        eventService.triggerInsert(result);
        return result;
    }

    // update!!! Should return request status
    public void endVisit(Integer visitId) {
        // Visit result = this.dao.updateVisitEndtime(LocalDateTime.now(), visitId);
        // eventService.triggerUpdate(result);
    }

    public List<Visit> getOngoingVisits() {
        return this.dao.getOngoingVisits();
    }

    public Visit getVisit(Integer visitId) {
        return this.dao.getVisit(visitId);
    }

}
