package io.github.unchangingconstant.studenttracker.app.controllers.components;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.controllers.ComponentUtils;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

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
                cellContent.textProperty().unbind();
                if (newVal != null) {
                    cellContent.textProperty().bind(cell.getItem().getFullLegalName());
                }
            });
            cell.setGraphic(cellContent);
            return cell;
        });
        selectionModelProperty().get().setSelectionMode(SelectionMode.MULTIPLE);
        selectionModelProperty().get().getSelectedItems().addListener(new ListChangeListener<>() {
            @Override
            public void onChanged(Change<? extends StudentModel> c) {
                System.out.println(c.getList());
            }
        });
    }

    @Override
    public void requestFocus() {}
    
}
