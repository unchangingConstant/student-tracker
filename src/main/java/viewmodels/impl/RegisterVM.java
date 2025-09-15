package viewmodels.impl;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import models.Model;
import viewmodels.ViewModel;

public class RegisterVM implements ViewModel<ObservableList<String>> {

    @FXML
    protected Label statedisplay;

    @FXML
    protected TextField nameInput;

    @Override
    public void setModel(Model<ObservableList<String>> model) {
    }

}
