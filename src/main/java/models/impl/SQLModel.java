package models.impl;

import java.util.List;
import java.util.NoSuchElementException;

import dao.SQLAccess;
import models.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;

public class SQLModel implements Model<ObservableList<String>> {

    private SimpleListProperty<String> rows;
    private SQLAccess database;

    public SQLModel(SQLAccess database) {
        this.rows = new SimpleListProperty<String>();
        this.database = database;
    }

    public void addRow(String row) {
        rows.add(row);
    }

    public void deleteRow(int index) {
        rows.remove(index);
    }

    @Override
    public void bind(ObjectProperty<ObservableList<String>> property) {
        rows.bind(property);
    }

}
