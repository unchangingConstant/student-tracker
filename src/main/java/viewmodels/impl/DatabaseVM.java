package viewmodels.impl;

import java.util.List;

import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import models.Model;
import models.impl.SQLModel;
import viewmodels.ViewModel;

public class DatabaseVM implements ViewModel<ObservableList<String>> {

    protected SQLModel model;

    @FXML
    protected ListView<String> studentList;

    @Override
    public void setModel(Model<ObservableList<String>> model) {
        this.model = (SQLModel) model;
        model.bind(studentList.itemsProperty());
    }

}
