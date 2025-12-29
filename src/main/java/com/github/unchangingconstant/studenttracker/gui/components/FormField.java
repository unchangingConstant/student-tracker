package com.github.unchangingconstant.studenttracker.gui.components;

import com.github.unchangingconstant.studenttracker.gui.ComponentUtils;
import com.github.unchangingconstant.studenttracker.gui.Controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
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
    super();
    ComponentUtils.hookIntoFXML(this, "/view/components/form_field.fxml");
  }

  @Override
  public void initialize() {
      fieldLabel.textProperty().bindBidirectional(fieldName);
      message.visibleProperty().bindBidirectional(messageVisible);
  }

}
