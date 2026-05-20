package com.github.unchangingconstant.studenttracker.guice;

import com.github.unchangingconstant.studenttracker.app.dbmanager.DatabaseObserver;
import com.github.unchangingconstant.studenttracker.app.entities.OngoingVisit;
import com.github.unchangingconstant.studenttracker.app.entities.Student;
import com.github.unchangingconstant.studenttracker.app.entities.Visit;
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

public class DatabaseManagerModule extends AbstractModule {

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
        dataSource.setUrl("JDBC:sqlite:database.db");
        return Jdbi.create(dataSource);
    }

    @Provides
    public DatabaseObserver<OngoingVisit> provideOngoingVisitDatabaseObserver() {
        return new DatabaseObserver<>();
    }

    @Provides
    public DatabaseObserver<Visit> provideVisitDatabaseObserver() {
        return new DatabaseObserver<>();
    }

    @Provides
    public DatabaseObserver<Student> provideStudentDatabaseObserver() {
        return new DatabaseObserver<>();
    }

}
