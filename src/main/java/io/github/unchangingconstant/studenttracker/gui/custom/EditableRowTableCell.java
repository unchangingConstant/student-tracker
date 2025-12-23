package io.github.unchangingconstant.studenttracker.gui.custom;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.TableCell;

public class EditableRowTableCell<T, U> extends TableCell<T, U> {

    private Node editEnabledGraphic = null;
    public void setEditEnabledGraphic(Node editEnabledGraphic) {this.editEnabledGraphic = editEnabledGraphic;}

    private Node editDisabledGraphic = null;
    public void setEditDisabledGraphic(Node editDisabledGraphic) {this.editDisabledGraphic = editDisabledGraphic;}

    private BooleanProperty rowEditEnabled = new SimpleBooleanProperty(false);
    public BooleanProperty rowEditEnabledProperty() {return rowEditEnabled;}

    public EditableRowTableCell() {
        super();
        rowEditEnabled.addListener((obs, oldVal, newVal) -> updateGraphic());
        itemProperty().addListener((obs, oldVal, newVal) -> updateGraphic());
    }

    private void updateGraphic() {
        if (getItem() == null) {
            setGraphic(null);
        } else {
            setGraphic(rowEditEnabled.get() ? editEnabledGraphic : editDisabledGraphic);
        }
    }

}
