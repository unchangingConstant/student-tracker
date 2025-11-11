package io.github.unchangingconstant.studenttracker.app.gui.custom;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.CustomComponentUtils;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StudentTableView extends TableView<Student> implements Controller {
    
    @FXML
    private TableColumn<Student, Boolean> actionsColumn;
    @FXML
    private TableColumn<Student, String> firstNameColumn;

    public StudentTableView()   {
        super();
        CustomComponentUtils.hookIntoFXML(this, "/view/components/student_table_view.fxml");
    }

    @Override
    public void initialize() {
        firstNameColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getFirstName());
        });
        setupActionsColumn();
    }

    private void setupActionsColumn()   {
        // Cell value is true if student is not null, false if otherwise
        // This value will be bound to the "Actions" combobox's visibility
        actionsColumn.setCellValueFactory(cellData ->   {
            return new SimpleBooleanProperty(cellData.getValue() == null ? false : true);
        });
        actionsColumn.setCellFactory(tableColumn -> {
            ComboBox<String> actionsMenu = new ComboBox<String>();
            actionsMenu.promptTextProperty().set("Actions");

            TableCell<Student, Boolean> cell = new TableCell<>();
            actionsMenu.visibleProperty().bind(cell.itemProperty());
            cell.setGraphic(actionsMenu);
            return cell;
        });
    }

}
