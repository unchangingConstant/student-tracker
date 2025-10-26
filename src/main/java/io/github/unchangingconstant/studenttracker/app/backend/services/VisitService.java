package io.github.unchangingconstant.studenttracker.app.backend.services;

import java.time.Instant;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.backend.dao.DatabaseDAO;
import io.github.unchangingconstant.studenttracker.app.backend.entities.OngoingVisit;
import io.github.unchangingconstant.studenttracker.app.backend.entities.Visit;

@Singleton
public class VisitService {

    private DatabaseDAO dao;
    private VisitEventService eventService;

    @Inject
    public VisitService(DatabaseDAO dao, VisitEventService eventService) {
        this.eventService = eventService;
        this.dao = dao;
    }

    public Integer startVisit(Integer studentId) {
        // Must not have ongoing visits when starting one
        Integer result = this.dao.insertVisit(Instant.now(), null, studentId);
        eventService.triggerInsert(result);
        return result;
    }

    // update!!! Should return request status
    public void endVisit(Integer visitId) {
        // Visit result = this.dao.updateVisitEndtime(LocalDateTime.now(), visitId);
        // eventService.triggerUpdate(result);
    }

    public Map<Integer, OngoingVisit> getOngoingVisits() {
        return this.dao.getOngoingVisits();
    }

    public Visit getVisit(Integer visitId) {
        return this.dao.getVisit(visitId);
    }

}
