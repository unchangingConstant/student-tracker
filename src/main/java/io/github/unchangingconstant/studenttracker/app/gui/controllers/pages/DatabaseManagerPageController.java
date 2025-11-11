package io.github.unchangingconstant.studenttracker.app.gui.controllers.pages;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.custom.ControlTableColumn;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class DatabaseManagerPageController implements Controller {
    
    @FXML 
    private TableView<String> studentTable;
    @FXML
    private ControlTableColumn<Student> actionsColumn;

    @Override
    public void initialize() {
        studentTable.getItems().add("Balls");
    }



}
