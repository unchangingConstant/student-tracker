package io.github.unchangingconstant.studenttracker.app.gui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class CustomComponentUtils {
    public static void hookIntoFXML(Node component, String fxmlPath)    {
        FXMLLoader loader = new FXMLLoader(component.getClass().getResource(fxmlPath));
        loader.setRoot(component);
        loader.setController(component);
        try {
            loader.load();
        }
        catch (IOException e)   {
            throw new RuntimeException(e);
      }
    }   
}
