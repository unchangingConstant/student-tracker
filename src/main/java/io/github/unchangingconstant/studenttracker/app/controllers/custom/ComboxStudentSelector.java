package io.github.unchangingconstant.studenttracker.app.controllers.custom;

import java.util.LinkedList;
import java.util.List;
import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.util.StringConverter;


/*
 * I'd take the example at this link: https://gist.github.com/floralvikings/10290131
 * But he added no support for binding. (huh?)
 */
public class ComboxStudentSelector extends ComboBox<StudentModel> implements Controller {

    private Property<ObservableList<StudentModel>> options = new SimpleListProperty<>(FXCollections.observableArrayList());
    public Property<ObservableList<StudentModel>> optionsProperty()    {return options;}

    public StringProperty textProperty() {return editorProperty().get().textProperty();} 

    public ComboxStudentSelector()  {
        super();
        CustomComponentUtils.hookIntoFXML(this, "/view/components/combox_student_selector.fxml");
    }

    @Override
    public void initialize() {
        /**
         * Setting this to null hands me complete control over the value selection mechanisms basically.
         * Don't remove this. Try it and see everything go to shit
         */
        selectionModelProperty().set(null);
        setEditable(true);
        setConverter(new StringConverter<StudentModel>() {
                @Override
                public String toString(StudentModel object) {
                    return object != null ? object.getFullLegalName().get() : null;
                }
                @Override
                public StudentModel fromString(String string) {
                    return valueProperty().get();
                } 
            });

        setCellFactory(listView -> {
            ComboBoxListCell<StudentModel> cell = new ComboBoxListCell<>();
            ObjectProperty<StudentModel> itemProp = cell.itemProperty();
            // cell.setConverter(new StringConverter<StudentModel>() {
            //     @Override
            //     public String toString(StudentModel object) {
            //         return object != null ? object.getFullLegalName().get() : null;
            //     }
            //     @Override
            //     public StudentModel fromString(String string) {
            //         return cell.itemProperty().get();
            //     } 
            // });
            cell.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    valueProperty().set(itemProp.get());
                }
            });
            return cell;
        });
        
        editorProperty().get().textProperty().addListener((obs,  s,  s2) -> {
                String input = s2.trim();
                if (s.equals(s2))   {
                    return;
                }
                if (input.length() == 0)    {
                    hide();
                } else {
                    List<StudentModel> matches = findMatches(input);
                    if (matches.size() == 0)    {
                    }
                    else {
                        itemsProperty().setValue(FXCollections.observableList(matches));
                        show();
                    }
                }
            }
        );
    }

    private List<StudentModel> findMatches(String input)   {
        LinkedList<StudentModel> newMatches = new LinkedList<>();
        options.getValue().forEach(student -> {
            String fullName = student.getFullLegalName().get();
            String prefName = student.getPrefName().get();
            if (fullName.contains(input) || prefName.contains(input))  {
                newMatches.add(student);
            }
        });
        return newMatches;
    }

}