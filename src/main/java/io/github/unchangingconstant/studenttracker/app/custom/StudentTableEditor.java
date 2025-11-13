package io.github.unchangingconstant.studenttracker.app.custom;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StudentTableEditor extends TableView<StudentModel> implements Controller {
    
    @FXML
    private TableColumn<StudentModel, Number> studentIdColumn;
    @FXML
    private TableColumn<StudentModel, String> firstNameColumn;
    @FXML
    private TableColumn<StudentModel, String> middleNameColumn;
    @FXML
    private TableColumn<StudentModel, String> lastNameColumn;
    @FXML
    private TableColumn<StudentModel, Number> subjectsColumn;
    @FXML
    private TableColumn<StudentModel, String> dateAddedColumn;
    @FXML
    private TableColumn<StudentModel, Number> actionsColumn;

    private Consumer<Integer> onEditAction;
    public void setOnEditAction(Consumer<Integer> onEditAction) {this.onEditAction = onEditAction;}

    private Consumer<Integer> onDeleteAction;
    public void setOnDeleteAction(Consumer<Integer> onDeleteAction)   {this.onDeleteAction = onDeleteAction;}


    public StudentTableEditor()   {
        super();
        CustomComponentUtils.hookIntoFXML(this, "/view/components/student_table_view.fxml");
    }

    @Override
    public void initialize() {
        studentIdColumn.setCellValueFactory(cellData -> {
            return cellData.getValue().getStudentId();
        });
        firstNameColumn.setCellValueFactory(cellData -> {
            return cellData.getValue().getFirstName();
        });
        middleNameColumn.setCellValueFactory(cellData -> {
            return cellData.getValue().getMiddleName();
        });
        lastNameColumn.setCellValueFactory(cellData -> {
            return cellData.getValue().getLastName();
        });
        subjectsColumn.setCellValueFactory(cellData -> {
            return cellData.getValue().getSubjects();
        });
        dateAddedColumn.setCellValueFactory(cellData -> {
            Instant startTime = cellData.getValue().getDateAdded().get();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd").withZone(ZoneId.systemDefault());
            return new SimpleStringProperty(formatter.format(startTime));
        });
        setupActionsColumn();
    }

    private void setupActionsColumn()   {
        actionsColumn.setCellValueFactory(cellData ->   {
            return cellData.getValue().getStudentId();
        });
        actionsColumn.setCellFactory(tableColumn -> {
            TableCell<StudentModel, Number> cell = new TableCell<>();
            ComboBox<String> actionsMenu = new ComboBox<>();
            actionsMenu.promptTextProperty().set("Actions");
            actionsMenu.getItems().addAll("Edit", "Delete");
            actionsMenu.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.equals("Edit"))    {
                    onEditAction.accept(cell.getItem().intValue());
                } else if (newVal.equals("Delete"))   {
                    onDeleteAction.accept(cell.getItem().intValue());
                }
            });

            actionsMenu.visibleProperty().bind(cell.itemProperty().isNotNull());
            cell.setGraphic(actionsMenu);
            return cell;
        });
    }

}
