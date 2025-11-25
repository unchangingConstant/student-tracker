package io.github.unchangingconstant.studenttracker.app.controllers.components;

import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

public class EditableListCell<T> extends ListCell<T>  {

    private final HBox cellLayout = new HBox();
    private final ComboBox<String> actionsMenu = new ComboBox<>();
    private final Button saveButton = new Button();

    private ObjectProperty<Callback<T, Node>> editorFactory = new SimpleObjectProperty<>(item -> new Label("Editor placeholder"));
    public void setEditorFactory(Callback<T, Node> editorFactory) {this.editorFactory.set(editorFactory);}

    private ObjectProperty<Callback<T, Node>> displayFactory = new SimpleObjectProperty<>(item -> new Label("Display placeholder"));
    public void setDisplayFactory(Callback<T, Node> displayFactory) {this.displayFactory.set(displayFactory);}

    private BooleanProperty editingEnabled = new SimpleBooleanProperty(false);
    public BooleanProperty editingEnabledProperty() {return editingEnabled;}

    private Runnable onSaveAction = () -> {};
    public void setOnSaveAction(Runnable onSaveAction) {this.onSaveAction = onSaveAction == null ? () -> {}: onSaveAction;}

    private Consumer<T> onDeleteAction = input -> {};
    public void setOnDeleteAction(Consumer<T> onDeleteAction)   {this.onDeleteAction = onDeleteAction == null ? input -> {}: onDeleteAction;}

    public EditableListCell() {
        super();
        setupLayout();
        setupActionsMenu();
        setupSaveButton();
        setupEditingEnabledProperty();
        setupFactoryProps();
        itemProperty().addListener((obs, oldVal, newVal) -> {
            updateGraphic();
        });
    }

    private void updateGraphic() {
        ObservableList<Node> children = cellLayout.getChildren();
        children.clear();
        if (getItem() == null) {
            return;
        }
        if (editingEnabled.get()) {
            Node editor = editorFactory.get().call(getItem());
            children.addAll(
                editor,
                saveButton
            );
        } else {
            Node display = displayFactory.get().call(getItem());
            children.addAll(
                display,
                actionsMenu
            );
        }
    }

    /**
     * Setup methods
     */

    private void setupLayout()  {
        cellLayout.setAlignment(Pos.CENTER_RIGHT);
        cellLayout.setFillHeight(true);
        HBox.setHgrow(saveButton, Priority.NEVER);
        HBox.setHgrow(actionsMenu, Priority.NEVER);
        setGraphic(cellLayout);
        updateGraphic();
    }

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
                    editingEnabled.set(true);
                } else if (action.equals("Delete")) {
                    onDeleteAction.accept(getItem());
                }
            });
            return comboCell;
        });
    }

    private void setupSaveButton() {
        saveButton.setText("Save");
        saveButton.setOnAction((actionEvent) -> onSaveAction.run());
    }

    private void setupEditingEnabledProperty() {
        ObservableList<Node> children = cellLayout.getChildren();
        editingEnabled.addListener((obs, oldVal, newVal) -> {
            children.clear();
            updateGraphic();
        });
    }

    private void setupFactoryProps() {
        editorFactory.addListener((obs, oldVal, newVal) -> {
            updateGraphic();
        });
        displayFactory.addListener((obs, oldVal, newVal) -> {
            updateGraphic();
        });
    }

    // Makes this component un-focusable
    @Override
    public void requestFocus()  {}

}
