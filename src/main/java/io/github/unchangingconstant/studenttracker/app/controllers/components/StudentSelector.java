package io.github.unchangingconstant.studenttracker.app.controllers.components;

import java.util.LinkedList;
import java.util.List;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.controllers.ComponentUtils;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

/*
 * I'd take the example at this link: https://gist.github.com/floralvikings/10290131
 * But he added no support for binding. (huh?)
 */
public class StudentSelector extends TextField implements Controller {

    private Property<ObservableList<StudentModel>> options = new SimpleListProperty<>(FXCollections.observableArrayList());
    public Property<ObservableList<StudentModel>> optionsProperty()    {return options;}

    private Property<StudentModel> selected = new SimpleObjectProperty<>(null);
    public Property<StudentModel> selectedProperty() {return selected;}

    private ContextMenu matchesPopup = new ContextMenu();

    public StudentSelector()  {
        super();
        ComponentUtils.hookIntoFXML(this, "/view/components/student_selector.fxml");
    }

    @Override
    public void initialize() {
        textProperty().addListener((obs,  s,  s2) -> {
                if (selected.getValue() != null && !getText().equals(selected.getValue().getFullLegalName().get()))   {
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
                    List<StudentModel> newMatches = findMatches();
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
        );
    }

    private LinkedList<StudentModel> findMatches()   {
        LinkedList<StudentModel> newMatches = new LinkedList<>();
        options.getValue().forEach(student -> {
            String fullName = student.getFullLegalName().get();
            String prefName = student.getPrefName().get();
            if (fullName.contains(getText()) || prefName.contains(getText()))  {
                newMatches.add(student);
            }
        });
        return newMatches;
    }

    private void populatePopup(List<StudentModel> items)    {
        // Clears current matches
        matchesPopup.getItems().clear();
        // Repopulate list with new matches
        items.forEach(student ->  {
            MenuItem newItem = new MenuItem(student.getFullLegalName().get());
            newItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    selected.setValue(student);
                    setText(student.getFullLegalName().get());
                }
            });
            matchesPopup.getItems().add(newItem);
        });
    }

}