package io.github.unchangingconstant.studenttracker.config;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleListener;
import org.jdbi.v3.core.Handles;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.sqlite.SQLiteDataSource;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;

public class DatabaseModule extends AbstractModule {

    @Provides
    @Singleton
    public static DatabaseDAO provideDatabaseDAO(Jdbi jdbi) {
        jdbi.getConfig(Handles.class).addListener(new HandleListener() {
            @Override
            public void handleCreated(Handle handle) {
                handle.execute("PRAGMA foreign_keys = ON");
            }
        });
        return jdbi.installPlugin(new SqlObjectPlugin()).onDemand(DatabaseDAO.class);
    }

    @Provides
    @Singleton
    public static Jdbi provideJdbi() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:database.db");
        return Jdbi.create(dataSource);
    }

}
