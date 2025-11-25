package io.github.unchangingconstant.studenttracker.app.controllers.components;

import java.util.function.Consumer;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class EditableListCell<T> extends ListCell<T> implements Controller {

    @FXML
    private ComboBox<String> actionsMenu;
    
    private Consumer<T> onEditAction = input -> {};
    public void setOnEditAction(Consumer<T> onEditAction) {this.onEditAction = onEditAction == null ? this.onEditAction: onEditAction;}

    private Consumer<T> onDeleteAction = input -> {};
    public void setOnDeleteAction(Consumer<T> onDeleteAction)   {this.onDeleteAction = onDeleteAction == null ? this.onDeleteAction: onDeleteAction;}

    public EditableListCell() {
        super();
        CustomComponentUtils.hookIntoFXML(this, "/view/custom/editable_list_cell.fxml");
    }

    @Override
    public void initialize() {
        actionsMenu.getItems().addAll("Edit", "Delete");
        actionsMenu.setSelectionModel(null);
        actionsMenu.setCellFactory(listView -> {
            ListCell<String> comboCell = new ListCell<>();
            /**
             * TODO make it so this only executes onClick. Or more specifically, when the the mouse is clicked and released
             * over the list cell. Not anything else.
             */
            comboCell.textProperty().bind(comboCell.itemProperty()); // TODO double check this line of code
            comboCell.setOnMouseReleased(mouseEvent -> {
                String action = comboCell.getItem();
                if (action.equals("Edit"))  {
                    onEditAction.accept(getItem());
                } else if (action.equals("Delete")) {
                    onDeleteAction.accept(getItem());
                }
            });
            return comboCell;
        });
        actionsMenu.visibleProperty().bind(itemProperty().isNotNull());
    }

}
