package io.github.unchangingconstant.studenttracker.app.gui.custom;

import java.io.IOException;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;

/*
 * Table column that can render controls in its cell
 */
public class ControlTableColumn<S> extends TableColumn<S, Void> {
    
    private final SimpleListProperty<Node> controls = new SimpleListProperty<>(FXCollections.observableArrayList());
    public Property<ObservableList<Node>> controlsProperty()  {return controls;}
    public ObservableList<Node> getControls()   {return controls.get();}

    public ControlTableColumn() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/components/control_table_column.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
        loader.load();
        }
        catch (IOException e)   {
        throw new RuntimeException(e);
        }
    }

}
