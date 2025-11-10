package io.github.unchangingconstant.studenttracker.app.gui.custom;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.stage.Window;

public class StudentSelector extends TextField implements Controller {
    
    private final PopupControl matchesPopup = new PopupControl();

    private ListView<Student> suggestionList = new ListView<>();

    public Property<ObservableList<Student>> optionsProperty() {return suggestionList.itemsProperty();}
    public ReadOnlyProperty<Student> selectedProperty() {return suggestionList.getSelectionModel().selectedItemProperty();}

    public StudentSelector()  {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/components/student_selector.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        }
        catch (IOException e)   {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize() {

        // Binds highlighted student to the TextField
        suggestionList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    setText(newVal.getFullName());
                }
            }
        );

        textProperty().addListener((obs, s, s2) -> {onTextChange(s, s2);});
    }

    private LinkedList<Student> findMatches()   {
        LinkedList<Student> newMatches = new LinkedList<>();
        optionsProperty().getValue().forEach(student -> {
            String name = student.getFullName();
            if (name.contains(getText()))  {
                newMatches.add(student);
            }
        });
        return newMatches;
    }

    private void populatePopup(List<Student> items)    {
        // Repopulate list with new matches
        List<Student> optionsList = optionsProperty().getValue();
        items.forEach(student -> optionsList.add(student));
    }

    private void onTextChange(String oldStr, String newStr) {
        if (getText().length() == 0)    {
            matchesPopup.hide();
        }
        else  {
            List<Student> newMatches = findMatches();
            if (newMatches.size() == 0)    {
                matchesPopup.hide();
            } 
            else {
                // Clears current matches
                optionsProperty().getValue().clear();
                populatePopup(newMatches);
                Window parent = getScene().getWindow();
                matchesPopup.show(parent, 
                    parent.getX() + this.localToScene(0, 0).getX() + this.getScene().getX(), 
                    parent.getY() + this.localToScene(0, 0).getY() + this.getScene().getY() + this.getBoundsInParent().getHeight());

            }
        }
    }

    class StudentSelectorSkin implements Skin<StudentSelector> {

        @Override
        public StudentSelector getSkinnable() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getSkinnable'");
        }

        @Override
        public Node getNode() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getNode'");
        }

        @Override
        public void dispose() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'dispose'");
        }

    }

}
