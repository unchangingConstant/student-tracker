package viewmodels.impl;

import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import models.Model;
import models.impl.SQLModel;
import viewmodels.ViewModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class RootVM implements ViewModel<ObservableList<String>> {

    private SQLModel model;

    @FXML
    protected Pane currentPage;
    @FXML
    protected ListView<String> databasePage;
    @FXML
    protected GridPane registerPage;
    @FXML
    protected DatabaseVM databasePageController;
    @FXML
    protected RegisterVM registerPageController;

    @Override
    public void setModel(Model<ObservableList<String>> model) {
        this.model = (SQLModel) model;
        databasePageController.setModel(model);
        registerPageController.setModel(model);
    }

    public void onRegisterButtonAction() {
        this.model.addRow(registerPageController.nameInput.getText());
    }

    public void onDeleteButtonAction() {
        this.model.deleteRow(0);
    }

}