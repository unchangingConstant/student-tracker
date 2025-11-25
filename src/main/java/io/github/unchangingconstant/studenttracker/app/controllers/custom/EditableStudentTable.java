package io.github.unchangingconstant.studenttracker.app.controllers.custom;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import io.github.unchangingconstant.studenttracker.app.controllers.components.EditableRowTable;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class EditableStudentTable extends EditableRowTable<StudentModel> implements Controller {
    
    public EditableStudentTable() {
        super();
        CustomComponentUtils.hookIntoFXML(this, "/view/components/editable_student_table.fxml");
    }

    @Override
    public void initialize() {
        setEditorFactory(student -> createEditor(student));
        setDisplayFactory(student -> createDisplay(student));
    }

    private HBox createEditor(StudentModel student) {
        HBox editor = new HBox();
        ObservableList<Node> children = editor.getChildren();
        return editor;
    }

    private HBox createDisplay(StudentModel student) {
        HBox display = new HBox();
        ObservableList<Node> children = display.getChildren();
    
        children.add(new Label(student.getStudentId().get().toString()));
        children.add(new Label(student.getFullLegalName().get()));
        children.add(new Label(student.getPrefName().get()));
        children.add(new Label(student.getSubjects().get().toString()));

        return display;
    }

}
