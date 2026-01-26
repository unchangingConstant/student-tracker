package com.github.unchangingconstant.studenttracker.gui.components;

import java.util.function.Consumer;

import com.github.unchangingconstant.studenttracker.app.entities.StudentQRCode;
import com.github.unchangingconstant.studenttracker.gui.ComponentUtils;
import com.github.unchangingconstant.studenttracker.gui.Controller;
import com.github.unchangingconstant.studenttracker.gui.models.StudentModel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class QRCodeTableView extends TableView<StudentModel> implements Controller {

    @FXML
    private TableColumn<StudentModel, Integer> studentIdColumn;
    @FXML
    private TableColumn<StudentModel, String> fullNameColumn;
    @FXML
    private TableColumn<StudentModel, String> qrCodeColumn;

    private final Property<Consumer<String>> onCopyButtonActionProperty =
        new SimpleObjectProperty<>(qrCode -> {});
    public void setOnCopyButtonAction(Consumer<String> function) {
        onCopyButtonActionProperty.setValue(function == null ? qrCode -> {} : function);
    }

    public QRCodeTableView() {
        super();
        ComponentUtils.hookIntoFXML(this, "/view/components/qr_code_table_view.fxml");
    }

    @Override
    public void initialize() {
        studentIdColumn.setCellValueFactory(cellData -> cellData.getValue().getStudentId());
        fullNameColumn.setCellValueFactory(cellData -> cellData.getValue().getFullName());
        setupQRCodeColumn();
    }

    private void setupQRCodeColumn() {

        qrCodeColumn.setCellFactory(tableView -> {
            QRCodeTableCell cell = new QRCodeTableCell();
            cell.onCopyButtonActionProperty.bind(onCopyButtonActionProperty);
            cell.labelTextProperty().bind(cell.itemProperty());
            return cell;
        });

        qrCodeColumn.setCellValueFactory(cellData -> {
            return Bindings.createStringBinding(
                () -> {
                    Integer studentId = cellData.getValue().getStudentId().get();
                    return StudentQRCode.createQrCode(studentId);
                },
                cellData.getValue().getStudentId()
            );
        });
    }

    private class QRCodeTableCell extends TableCell<StudentModel, String> {

        private final TextField qrCodeDisplay = new TextField();
        private final Button copyButton = new Button("Copy");
        
        private final Property<Consumer<String>> onCopyButtonActionProperty = 
            new SimpleObjectProperty<>(qrCode -> {});

        private QRCodeTableCell() {
            qrCodeDisplay.setEditable(false);
            copyButton.onActionProperty().bind(
                Bindings.createObjectBinding(
                    // Factory that creates EventHandler according to new Consumer
                    () -> {
                        // EventHandler with the new Consumer
                        return actionEvent -> {
                            onCopyButtonActionProperty.getValue().accept(this.getItem());
                        };
                    }, 
                    onCopyButtonActionProperty)
            );
            HBox graphic = new HBox(); 
            graphic.getChildren().addAll(qrCodeDisplay, copyButton);
            setGraphic(graphic);
        }

        private StringProperty labelTextProperty() {
            return qrCodeDisplay.textProperty();
        }

    }

    @Override
    public void requestFocus() {
    }
    
}
