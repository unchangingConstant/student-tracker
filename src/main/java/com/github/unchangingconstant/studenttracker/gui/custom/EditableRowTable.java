package com.github.unchangingconstant.studenttracker.gui.custom;

import java.util.Comparator;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

/**
 * T is the value the table holds, U is the value of the actions column, which has to be manually 
 * set when this class is extended.
 */
public abstract class EditableRowTable<T, U> extends TableView<T> {

    private final TableColumn<T, U> controlColumn = new TableColumn<>();
    public final TableColumn<T, U> getControlColumn() {return controlColumn;}

    // Represents current edited row's index
    // If -1, no index is selected
    private final SimpleIntegerProperty editedRowIndex = new SimpleIntegerProperty(-1);
    public final SimpleIntegerProperty editedRowIndexProperty() {return editedRowIndex;}

    private final BooleanProperty actionsDisabled = new SimpleBooleanProperty(false);
    public final BooleanProperty actionsDisabledProperty() {return actionsDisabled;}

    private final ObjectProperty<Consumer<Integer>> onEditAction = new SimpleObjectProperty<>(input -> {});
    public final ObjectProperty<Consumer<Integer>> onEditActionProperty() {return onEditAction;}

    private final ObjectProperty<Runnable> onSaveAction = new SimpleObjectProperty<>(() -> {});
    public final ObjectProperty<Runnable> onSaveActionProperty() {return onSaveAction;}

    private final ObjectProperty<Consumer<U>> onDeleteAction = new SimpleObjectProperty<>(input -> {});
    public final ObjectProperty<Consumer<U>> onDeleteActionProperty()   {return onDeleteAction;}

    public EditableRowTable()   {
        super();
        setupControlColumn();
        onEditAction.set(editedRowIndex::set);
        // If a row is being edited, disable all action combos
        actionsDisabled.bind(editedRowIndex.isNotEqualTo(-1));
    }

    /*
     * Define the control cells' value factory upon implementation
     */
    protected abstract Callback<CellDataFeatures<T, U>, ObservableValue<U>> getControlCellValueFactory();

    private void setupControlColumn() {
        controlColumn.setCellValueFactory(cellData -> getControlCellValueFactory().call(cellData));
        controlColumn.setCellFactory(tableColumn -> {
            EditableRowTableControlCell<T, U> controlsCell = new EditableRowTableControlCell<>();
            controlsCell.rowEditEnabledProperty().bind(editedRowIndex.isEqualTo(controlsCell.indexProperty()));
            controlsCell.actionsDisabledProperty().bind(actionsDisabled);
            controlsCell.setOnEditAction(index -> onEditAction.get().accept(index));
            controlsCell.setOnSaveAction(() -> onSaveAction.get().run());
            controlsCell.setOnDeleteAction(item -> onDeleteAction.get().accept(item));
            // System.out.println(controlsCell);
            return controlsCell;
        });
        getColumns().add(controlColumn);
    }

    // Makes this component un-focusable
    @Override
    public void requestFocus()  {}
    
}
