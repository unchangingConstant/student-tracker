package io.github.unchangingconstant.studenttracker.app.gui.controllers.pages;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.custom.StudentTableView;
import io.github.unchangingconstant.studenttracker.app.gui.models.StudentTableModel;
import javafx.fxml.FXML;

public class DatabaseManagerPageController implements Controller {
    
    @FXML 
    private StudentTableView studentTable;

    private StudentTableModel model;

    @Inject
    public DatabaseManagerPageController(StudentTableModel model)  {
        this.model = model;
    }

    @Override
    public void initialize() {
        model.bind(studentTable.itemsProperty());
        System.out.println(model.getStudents());
        System.out.println(studentTable.itemsProperty().getValue());
    }



}
