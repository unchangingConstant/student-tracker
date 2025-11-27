package io.github.unchangingconstant.studenttracker.app.controllers.custom;

import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

public class EditableRowTableControlCell<T, U> extends EditableRowTableCell<T, U>  {

    private final ComboBox<String> actionsMenu = new ComboBox<>();
    private final Button saveButton = new Button();

    public BooleanProperty actionsDisabledProperty() {return actionsMenu.disableProperty();}

    private Consumer<Integer> onEditAction = input -> {};
    public void setOnEditAction(Consumer<Integer> onEditAction) {this.onEditAction = onEditAction == null ? input -> {}: onEditAction;}

    private Runnable onSaveAction = () -> {};
    public void setOnSaveAction(Runnable onSaveAction) {this.onSaveAction = onSaveAction == null ? () -> {}: onSaveAction;}

    private Consumer<U> onDeleteAction = input -> {};
    public void setOnDeleteAction(Consumer<U> onDeleteAction)   {this.onDeleteAction = onDeleteAction == null ? input -> {}: onDeleteAction;}

    public EditableRowTableControlCell() {
        super();
        setupActionsMenu();
        setupSaveButton();
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
            /*
             * TODO make it so this only executes onClick. Or more specifically, when the the mouse is clicked and released
             * over the list cell. Not anything else.
             * 
             * You may be asking, why not use onClick? I've fucking tried man. A lot of weirdness with how ComboBox cells 
             * work I think.
             */
            comboCell.textProperty().bind(comboCell.itemProperty()); // Yes, we need this
            comboCell.setOnMouseReleased(mouseEvent -> {
                String action = comboCell.getItem();
                if (action.equals("Edit"))  {
                    onEditAction.accept(getIndex());
                } else if (action.equals("Delete")) {
                    onDeleteAction.accept(getItem());
                }
                actionsMenu.hide();
            });
            return comboCell;
        });
        setEditDisabledGraphic(actionsMenu);
    }

    private void setupSaveButton() {
        saveButton.setText("Save");
        saveButton.setOnAction((actionEvent) -> onSaveAction.run());
        setEditEnabledGraphic(saveButton);
    }

    // Makes this component un-focusable
    @Override
    public void requestFocus()  {}

}
