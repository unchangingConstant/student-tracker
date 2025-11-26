package io.github.unchangingconstant.studenttracker.app.controllers.components;

import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

/**
 * T is the value the table holds, U is the value of the actions column, which as to be manually 
 * set when this class is extended.
 */
public abstract class EditableRowTable<T, U> extends TableView<T> {

    // Holds currently edited row item
    // Seleceted index thingy

    private final TableColumn<T, U> actionsColumn = new TableColumn<>();
    public TableColumn<T, U> getActionsColumn() {return actionsColumn;}

    private BooleanProperty actionsDisabled = new SimpleBooleanProperty(true);
    public BooleanProperty actionsDisabledProperty() {return actionsDisabled;}

    private ObjectProperty<Consumer<Integer>> onEditAction = new SimpleObjectProperty<>(input -> {});
    public ObjectProperty<Consumer<Integer>> onEditActionProperty() {return onEditAction;}

    private ObjectProperty<Runnable> onSaveAction = new SimpleObjectProperty<>(() -> {});
    public ObjectProperty<Runnable> onSaveActionProperty() {return onSaveAction;}

    private ObjectProperty<Consumer<U>> onDeleteAction = new SimpleObjectProperty<>(input -> {});
    public ObjectProperty<Consumer<U>> onDeleteActionProperty()   {return onDeleteAction;}

    public EditableRowTable()   {
        super();
        setupControlColumn();
    }

    /*
     * Define the control cell value factory for this
     */
    protected abstract Callback<CellDataFeatures<T, U>, ObservableValue<U>> getControlCellValueFactory();

    private void setupControlColumn() {
        actionsColumn.setCellValueFactory(cellData -> getControlCellValueFactory().call(cellData));
        // TODO optimize at some point. Do we need to bind a million things?
        // TODO also think, is there some way to add a listener to the SelectionModel such that work is only done for the rows that need updating?
        // (as opposed to binding a bunch of properties to the selection model) 
        actionsColumn.setCellFactory(tableColumn -> {
            EditableRowTableControlCell<T, U> controlsCell = new EditableRowTableControlCell<>();
            //controlsCell.rowEditEnabledProperty().bind(selectionModel.selectedIndexProperty().isEqualTo(controlsCell.indexProperty())); // Optimize this
            controlsCell.onEditActionProperty().bind(onEditAction);
            controlsCell.onSaveActionProperty().bind(onSaveAction);
            controlsCell.onDeleteActionProperty().bind(onDeleteAction);
            return controlsCell;
        });
        getColumns().add(actionsColumn);
    }

    // Makes this component un-focusable
    @Override
    public void requestFocus()  {}
    
}
