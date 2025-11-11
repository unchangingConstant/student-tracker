package io.github.unchangingconstant.studenttracker.app.gui.custom;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.CustomComponentUtils;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

/*
 * I'd take the example at this link: https://gist.github.com/floralvikings/10290131
 * But that idiot added no support for binding.
 */
public class StudentSelector extends TextField implements Controller {

    private Property<ObservableList<Student>> options = new SimpleListProperty<>(FXCollections.observableArrayList());
    public Property<ObservableList<Student>> optionsProperty()    {return options;}

    private Property<Student> selected = new SimpleObjectProperty<>(null);
    public Property<Student> selectedProperty() {return selected;}

    private ContextMenu matchesPopup = new ContextMenu();

    public StudentSelector()  {
        super();
        CustomComponentUtils.hookIntoFXML(this, "/view/components/student_selector.fxml");
    }

    @Override
    public void initialize() {
        textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                if (selected.getValue() != null && !getText().equals(selected.getValue().getFullName()))   {
                    // when a selection is made, the text changes. When the text changes,
                    // this method is called again. Issues arise from this lol
                    // Hopefully this block fixes it. But there's got to be a better way
                    selected.setValue(null);
                }
                if (getText().length() == 0)    {
                    matchesPopup.hide();
                    selected.setValue(null);
                } 
                else  {
                    List<Student> newMatches = findMatches();
                    if (newMatches.size() == 0)    {
                        matchesPopup.hide();
                        selected.setValue(null);
                    } 
                    else {
                        populatePopup(newMatches);
                        matchesPopup.show(StudentSelector.this, Side.BOTTOM, 0, 0);
                    }
                }
            }
        });
    }

    private LinkedList<Student> findMatches()   {
        LinkedList<Student> newMatches = new LinkedList<>();
        options.getValue().forEach(student -> {
            String name = student.getFullName();
            if (name.contains(getText()))  {
                newMatches.add(student);
            }
        });
        return newMatches;
    }

    private void populatePopup(List<Student> items)    {
        // Clears current matches
        matchesPopup.getItems().clear();
        // Repopulate list with new matches
        items.forEach(student ->  {
            MenuItem newItem = new MenuItem(student.getFullName());
            newItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    selected.setValue(student);
                    setText(student.getFullName());
                }
            });
            matchesPopup.getItems().add(newItem);
        });
    }

}