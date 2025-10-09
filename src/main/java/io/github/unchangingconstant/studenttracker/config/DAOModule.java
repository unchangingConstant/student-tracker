package io.github.unchangingconstant.studenttracker.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.sqlite.SQLiteDataSource;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;

public class DAOModule extends AbstractModule {

    @Provides
    @Singleton
    public DatabaseDAO provideDatabaseDAO() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:database.db");
        return Jdbi.create(dataSource).installPlugin(new SqlObjectPlugin()).onDemand(DatabaseDAO.class);
    }

}
