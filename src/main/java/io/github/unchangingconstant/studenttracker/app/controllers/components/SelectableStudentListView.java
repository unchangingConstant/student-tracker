package io.github.unchangingconstant.studenttracker.app.controllers.components;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.controllers.ComponentUtils;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

/**
 * This components only job is to provide an interface for the user to select a student.
 */
public class SelectableStudentListView extends ListView<StudentModel> implements Controller {

    public SelectableStudentListView() {
        super();
        ComponentUtils.hookIntoFXML(this, "/view/components/selectable_student_list_view.fxml");
    }

    @Override
    public void initialize() {
        setCellFactory(listView -> {
            ListCell<StudentModel> cell = new ListCell<StudentModel>();
            Label cellContent = new Label();
            cell.itemProperty().addListener((obs, oldVal, newVal) -> {
                cellContent.setText(null);
                if (newVal != null) {
                    cellContent.setText(cell.getItem().getFullLegalName().get());
                }
            });
            cell.setGraphic(cellContent);
            return cell;
        });
    }

    @Override
    public void requestFocus() {}
    
}
