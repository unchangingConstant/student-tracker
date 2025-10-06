package io.github.unchangingconstant.studenttracker.context.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.github.unchangingconstant.studenttracker.app.dao.AttendanceDAO;

public class DAOModule extends AbstractModule {

    @Provides
    public static AttendanceDAO provideAttendanceDAO() {
        return null;
    }

}
