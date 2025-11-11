package io.github.unchangingconstant.studenttracker.app.gui.controllers.pages;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.custom.StudentTableView;
import io.github.unchangingconstant.studenttracker.app.gui.viewmodels.DatabaseManagerViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DatabaseManagerPageController implements Controller {
    
    @FXML 
    private StudentTableView studentTable;
    @FXML
    private Button addStudentButton;

    private DatabaseManagerViewModel viewModel;

    @Inject
    public DatabaseManagerPageController(DatabaseManagerViewModel viewModel)  {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize() {
        viewModel.bindToStudentTable(studentTable.itemsProperty());
        studentTable.setOnDeleteAction((studentId) -> viewModel.onDeleteAction(studentId));
    }
}
