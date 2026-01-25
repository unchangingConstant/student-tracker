package com.github.unchangingconstant.studenttracker.guice;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleListener;
import org.jdbi.v3.core.Handles;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.sqlite.SQLiteDataSource;

import com.github.unchangingconstant.studenttracker.app.dbmanager.AttendanceDAO;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class DAOModule extends AbstractModule {

    @Provides
    @Singleton
    public AttendanceDAO provideDatabaseDAO(Jdbi jdbi) {
        jdbi.getConfig(Handles.class).addListener(new HandleListener() {
            @Override
            public void handleCreated(Handle handle) {
                handle.execute("PRAGMA foreign_keys = ON");
            }
        });
        return jdbi.installPlugin(new SqlObjectPlugin()).onDemand(AttendanceDAO.class);
    }

    @Provides
    @Singleton
    public Jdbi provideJdbi() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:database.db");
        return Jdbi.create(dataSource);
    }

}
