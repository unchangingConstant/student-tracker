package io.github.unchangingconstant.studenttracker.app.controllers.components;

import java.util.function.Consumer;

import io.github.unchangingconstant.studenttracker.app.Controller;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class EditableRowTable<T> extends ListView<T> implements Controller{

    private Callback<ListView<T>, EditableListCell<T>> graphicFactory;

    private Consumer<T> onEditAction = input -> {};
    public void setOnEditAction(Consumer<T> onEditAction) {this.onEditAction = onEditAction == null ? this.onEditAction: onEditAction;}

    private Consumer<T> onDeleteAction = input -> {};
    public void setOnDeleteAction(Consumer<T> onDeleteAction)   {this.onDeleteAction = onDeleteAction == null ? this.onDeleteAction: onDeleteAction;}

    public EditableRowTable()   {
        super();
        setCellFactory(listView -> new EditableListCell<T>());
    }

    @Override
    public void initialize() {
        // setCellFactory(listView -> new EditableListCell());
    }
    
}
