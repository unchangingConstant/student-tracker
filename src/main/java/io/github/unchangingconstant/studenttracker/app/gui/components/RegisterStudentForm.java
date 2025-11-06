package io.github.unchangingconstant.studenttracker.app.gui.components;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

public class RegisterStudentForm extends BorderPane {
    
    private final Label firstNameLabel = new Label("First Name");
    private final Label lastNameLabel = new Label("Last Name");
    private final Label middleNameLabel = new Label("Middle Name");
    private final Label subjectsLabel = new Label("Subjects");

    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final TextField middleNameField = new TextField();
    private final TextField subjectsField = new TextField();

    private final StringConverter formatter = new StringConverter<String>()     {

        @Override
        public String toString(String string) {
            return null;
        }

        @Override
        public String fromString(String string) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'fromString'");
        }
        
    };

    public RegisterStudentForm()    {
        super();

    }

}
