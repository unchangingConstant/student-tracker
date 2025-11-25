package io.github.unchangingconstant.studenttracker.app.controllers.components;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

public class EditableRowTable<T> extends ListView<T> {

    private Callback<T, Node> displayFactory;
    public void setDisplayFactory(Callback<T, Node> displayFactory) {this.displayFactory = displayFactory;} 

    private Callback<T, Node> editorFactory;
    public void setEditorFactory(Callback<T, Node> editorFactory) {this.editorFactory = editorFactory;}

    private Consumer<T> onDeleteAction = input -> {};
    public void setOnDeleteAction(Consumer<T> onDeleteAction) {this.onDeleteAction = onDeleteAction == null ? input -> {}: onDeleteAction;}

    private Runnable onSaveAction = () -> {};
    public void setOnSaveAction(Runnable onSaveAction) {this.onSaveAction = onSaveAction == null ? () -> {}: onSaveAction;}

    public EditableRowTable()   {
        super();
        setCellFactory(listView -> {
            EditableListCell<T> cell = new EditableListCell<>();
            cell.setOnSaveAction(() -> onSaveAction.run());
            cell.setOnDeleteAction((item) -> onDeleteAction.accept(item));
            cell.setEditorFactory((student) -> {
                Node editor = editorFactory.call(student);
                HBox.setHgrow(editor, Priority.ALWAYS);
                return editor;
            });
            cell.setDisplayFactory((student) -> {
                Node display = displayFactory.call(student);
                HBox.setHgrow(display, Priority.ALWAYS);
                return display;
            });
            return cell;
        });   
    }

    // Makes this component un-focusable
    @Override
    public void requestFocus()  {}
    
}
