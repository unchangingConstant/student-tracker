package io.github.unchangingconstant.studenttracker.app.controllers.custom;

import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import io.github.unchangingconstant.studenttracker.app.Controller;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXML;

public class Header extends StackPane implements Controller {
    
    @FXML
    private MenuBar menuBar;



    public Header() {
        super();
        CustomComponentUtils.hookIntoFXML(this, "/view/components/header.fxml");
    }

    @Override
    public void initialize()    {
    }

}
