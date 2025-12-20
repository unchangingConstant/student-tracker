package io.github.unchangingconstant.studenttracker.app.controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;

public class ComponentUtils  {
    public static void hookIntoFXML(Object component, String fxmlPath)    {
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
