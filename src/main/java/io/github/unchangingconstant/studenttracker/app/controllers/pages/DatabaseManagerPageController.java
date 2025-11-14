package io.github.unchangingconstant.studenttracker.app.controllers.pages;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.custom.StudentEditor;
import io.github.unchangingconstant.studenttracker.app.custom.StudentTableEditor;
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
        studentEditor.setOnAction(actionEvent -> viewModel.onSaveAction());
        // If editingEnabled changes, the displayed component changes to reflect that value.
        // See edittingEnabled property in DatabaseManagerViewModel
        editorContainer.getChildren().remove(studentEditor);
        viewModel.editingEnabledProperty().addListener((obs, oldVal, edittingEnabled) -> {
            if (edittingEnabled)    {
                editorContainer.getChildren().remove(addStudentButton);
                editorContainer.getChildren().add(studentEditor);
            } else  {
                editorContainer.getChildren().remove(studentEditor);
                editorContainer.getChildren().add(addStudentButton);
            }
        });

        addStudentButton.setOnAction(actionEvent -> viewModel.onAddStudentButtonAction());

        studentEditor.fullLegalNameTextProperty().bindBidirectional(viewModel.currentEditedStudentProperty().get().getFullLegalName());
        studentEditor.prefNameTextProperty().bindBidirectional(viewModel.currentEditedStudentProperty().get().getPrefName());
        viewModel.currentEditedStudentProperty().get().getSubjects().bind(studentEditor.subjectsProperty());
        studentTable.actionsEnabledProperty().bind(viewModel.editingEnabledProperty().not());
    }
}
