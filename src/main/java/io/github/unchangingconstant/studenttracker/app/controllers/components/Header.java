package io.github.unchangingconstant.studenttracker.app.controllers.components;

import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import io.github.unchangingconstant.studenttracker.app.Controller;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class Header extends StackPane implements Controller {
    
    @FXML
    private Menu menuButton;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Label title;

    public ObservableList<MenuItem> getItems() {return menuButton.getItems();}
    public ObservableList<Menu> getMenus() {return menuBar.getMenus();}
    public String getText() {return title.getText();}
    public void setText(String newText) {title.setText(newText);}

    public Header() {
        super();
        CustomComponentUtils.hookIntoFXML(this, "/view/components/header.fxml");
    }

    @Override
    public void initialize()    {
    }

}
