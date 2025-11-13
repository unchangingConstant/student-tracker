package io.github.unchangingconstant.studenttracker.app.controllers.pages;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.custom.StudentEditor;
import io.github.unchangingconstant.studenttracker.app.custom.StudentTableEditor;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import io.github.unchangingconstant.studenttracker.app.viewmodels.DatabaseManagerViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class DatabaseManagerPageController implements Controller {
    
    @FXML 
    private StudentTableEditor studentTable;
    @FXML
    private Button addStudentButton;
    @FXML
    private StudentEditor studentEditor;
    @FXML
    private HBox editorContainer;

    private DatabaseManagerViewModel viewModel;

    @Inject
    public DatabaseManagerPageController(DatabaseManagerViewModel viewModel)  {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize() {
        viewModel.bindToStudentTable(studentTable.itemsProperty());
        studentTable.setOnDeleteAction((studentId) -> viewModel.onDeleteAction(studentId));
        studentEditor.setOnAction(actionEvent -> viewModel.onSaveAction(new StudentModel(null, 
            studentEditor.firstNameTextProperty().get(), 
            studentEditor.middleNameTextProperty().get(), 
            studentEditor.lastNameTextProperty().get(), 
            null, 
            1)));

        editorContainer.getChildren().remove(addStudentButton);

    }
}
