package io.github.unchangingconstant.studenttracker.app.gui.components;

import javafx.scene.layout.VBox;

public class RegisterStudentForm extends VBox {

    private final FormField firstNameField = new FormField();
    private final FormField lastNameField = new FormField();
    private final FormField middleNameField = new FormField();
    private final FormField subjectsField = new FormField();

    public RegisterStudentForm()    {
        super();
        getChildren().addAll(firstNameField, lastNameField, middleNameField, subjectsField);
        style();
    }

    private void style()    {
        firstNameField.setMaxWidth(Double.MAX_VALUE);
        lastNameField.setMaxWidth(Double.MAX_VALUE);
        middleNameField.setMaxWidth(Double.MAX_VALUE);
        subjectsField.setMaxWidth(Double.MAX_VALUE);
    }

}
