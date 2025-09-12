package viewmodels.impl;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import models.Model;
import viewmodels.ViewModel;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class RootVM implements ViewModel {

    @FXML
    private Label stateDisplay;
    @FXML
    private Pane currentPage;
    @FXML
    private TableView databasePage;
    @FXML
    private GridPane registerPage;

    public void onDatabaseButtonAction() {
        stateDisplay.setText("Database!");
        databasePage.setVisible(true);
        registerPage.setVisible(false);
    }

    public void onRegisterButtonAction() {
        stateDisplay.setText("Register!");
        databasePage.setVisible(false);
        registerPage.setVisible(true);
    }

    @Override
    public void setModel(Model model) {
        throw new IllegalStateException("This ViewModel doesn't observe a Model");
    }

}