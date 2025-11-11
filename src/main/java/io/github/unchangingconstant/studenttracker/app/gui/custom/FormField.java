package io.github.unchangingconstant.studenttracker.app.gui.custom;

import java.io.IOException;

import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class FormField extends VBox implements Controller {
    
    @FXML
    private Label fieldLabel;
    @FXML
    private TextField input;
    @FXML
    private Label message;

    // The field name of this FormField
    private final StringProperty fieldName = new SimpleStringProperty("");
    public void setFieldName(String newText) {fieldName.set(newText);}
    public String getFieldName() {return fieldName.get();}

    private final BooleanProperty messageVisible = new SimpleBooleanProperty(false);
    public void setMessageVisible(boolean newBool) {messageVisible.set(newBool);}
    public Boolean getMessageVisible() {return messageVisible.get();}

    public StringProperty textProperty() {return input.textProperty();}
    public void setText(String newText) {input.textProperty().set(newText);}
    public String getText() {return input.textProperty().get();}

    public FormField()  {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/components/form_field.fxml"));
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
        fieldLabel.textProperty().bindBidirectional(fieldName);
        message.visibleProperty().bindBidirectional(messageVisible);
    }

}
