package com.github.unchangingconstant.studenttracker.gui.components;

import com.github.unchangingconstant.studenttracker.app.domain.StudentQRCodeDomain;
import com.github.unchangingconstant.studenttracker.gui.ComponentUtils;
import com.github.unchangingconstant.studenttracker.gui.Controller;
import com.github.unchangingconstant.studenttracker.gui.models.StudentModel;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class QRCodeTableView extends TableView<StudentModel> implements Controller {

    @FXML
    private TableColumn<StudentModel, Integer> studentIdColumn;
    @FXML
    private TableColumn<StudentModel, String> fullNameColumn;
    @FXML
    private TableColumn<StudentModel, String> qrCodeColumn;

    public QRCodeTableView() {
        super();
        ComponentUtils.hookIntoFXML(this, "/view/components/qr_code_table_view.fxml");
    }

    @Override
    public void initialize() {
        studentIdColumn.setCellValueFactory(cellData -> cellData.getValue().getStudentId());
        fullNameColumn.setCellValueFactory(cellData -> cellData.getValue().getFullLegalName());
        setupQRCodeColumn();
    }

    private void setupQRCodeColumn() {

        // qrCodeColumn.setCellFactory(tableView -> {
        //     TableCell<StudentModel, String> cell = new TableCell<>();
        //     return null;
        // });

        qrCodeColumn.setCellValueFactory(cellData -> {
            return Bindings.createStringBinding(
                () -> {
                    Integer studentId = cellData.getValue().getStudentId().get();
                    return StudentQRCodeDomain.HEADER 
                        + String.valueOf(studentId) 
                        + StudentQRCodeDomain.SEPERATOR 
                        + Integer.toHexString(studentId) 
                        + StudentQRCodeDomain.FOOTER;
                },
                cellData.getValue().getStudentId()
            );
        });
    }

    @Override
    public void requestFocus() {
    }
    
}
