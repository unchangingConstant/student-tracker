package models.impl;

import java.util.List;

import dao.SQLiteAccess;
import models.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SQLModel implements Model<ObservableList<String>> {

    private SimpleListProperty<String> rows;
    private SQLiteAccess database;

    public SQLModel(SQLiteAccess database) {
        List<String> initialData = database.getStudents(null);
        System.out.println(initialData);
        // This way. Otherwise the property has a null list
        this.rows = new SimpleListProperty<String>(FXCollections.observableArrayList(initialData));
        this.database = database;
    }

    public void addRow(String row) {
        System.out.println("we adding");
        rows.add(row);
    }

    public void deleteRow(int index) {
        rows.remove(index);
    }

    @Override
    public void bind(ObjectProperty<ObservableList<String>> property) {
        property.bind(rows);
    }

}
