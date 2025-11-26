package io.github.unchangingconstant.studenttracker.app.controllers.components;

import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;

public class EditableRowTableControlCell<T, U> extends TableCell<T, U>  {

    private final ComboBox<String> actionsMenu = new ComboBox<>();
    private final Button saveButton = new Button();

    public BooleanProperty actionsDisabledProperty() {return actionsMenu.disableProperty();}

    private BooleanProperty rowEditEnabled = new SimpleBooleanProperty(false);
    public BooleanProperty rowEditEnabledProperty() {return rowEditEnabled;}

    private ObjectProperty<Consumer<Integer>> onEditAction = new SimpleObjectProperty<>(input -> {});
    public ObjectProperty<Consumer<Integer>> onEditActionProperty() {return onEditAction;}

    private ObjectProperty<Runnable> onSaveAction = new SimpleObjectProperty<>(() -> {});
    public ObjectProperty<Runnable> onSaveActionProperty() {return onSaveAction;}

    private ObjectProperty<Consumer<U>> onDeleteAction = new SimpleObjectProperty<>(input -> {});
    public ObjectProperty<Consumer<U>> onDeleteActionProperty()   {return onDeleteAction;}

    public EditableRowTableControlCell() {
        super();
        setupActionsMenu();
        setupSaveButton();
        rowEditEnabled.addListener((obs, oldVal, newVal) -> {
            updateGraphic();
        });
        itemProperty().addListener((obs, oldVal, newVal) -> {
            updateGraphic();
        });
        updateGraphic();
    }

    private void updateGraphic() {
        if (itemProperty().get() == null) {
            setGraphic(null);
        } else if (rowEditEnabled.get()) {
            setGraphic(saveButton);
        } else {
            setGraphic(actionsMenu);
        }
    }

    /**
     * Setup methods
     */

    private void setupActionsMenu() {
        actionsMenu.setPromptText("Actions");
        actionsMenu.getItems().addAll("Edit", "Delete");
        actionsMenu.setSelectionModel(null);
        actionsMenu.setCellFactory(listView -> {
            ListCell<String> comboCell = new ListCell<>();
            /**
             * TODO make it so this only executes onClick. Or more specifically, when the the mouse is clicked and released
             * over the list cell. Not anything else.
             */
            comboCell.textProperty().bind(comboCell.itemProperty()); // Yes, we need this
            comboCell.setOnMouseReleased(mouseEvent -> {
                String action = comboCell.getItem();
                if (action.equals("Edit"))  {
                    onEditAction.get().accept(getIndex());
                } else if (action.equals("Delete")) {
                    onDeleteAction.get().accept(getItem());
                }
            });
            return comboCell;
        });
    }

    private void setupSaveButton() {
        saveButton.setText("Save");
        saveButton.setOnAction((actionEvent) -> onSaveAction.get().run());
    }

    // Makes this component un-focusable
    @Override
    public void requestFocus()  {}

}
