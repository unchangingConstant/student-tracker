package io.github.unchangingconstant.studenttracker.app.gui.custom;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.CustomComponentUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StudentTableView extends TableView<Student> implements Controller {
    
    @FXML
    private TableColumn<Student, Number> studentIdColumn;
    @FXML
    private TableColumn<Student, String> firstNameColumn;
    @FXML
    private TableColumn<Student, String> middleNameColumn;
    @FXML
    private TableColumn<Student, String> lastNameColumn;
    @FXML
    private TableColumn<Student, Number> subjectsColumn;
    @FXML
    private TableColumn<Student, String> dateAddedColumn;
    @FXML
    private TableColumn<Student, Number> actionsColumn;

    private Consumer<Integer> onEditAction;
    public void setOnEditAction(Consumer<Integer> onEditAction) {this.onEditAction = onEditAction;}

    private Consumer<Integer> onDeleteAction;
    public void setOnDeleteAction(Consumer<Integer> onDeleteAction)   {this.onDeleteAction = onDeleteAction;}


    public StudentTableView()   {
        super();
        CustomComponentUtils.hookIntoFXML(this, "/view/components/student_table_view.fxml");
    }

    @Override
    public void initialize() {
        studentIdColumn.setCellValueFactory(cellData -> {
            return new SimpleIntegerProperty(cellData.getValue().getStudentId());
        });
        firstNameColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getFirstName());
        });
        middleNameColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getMiddleName());
        });
        lastNameColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getLastName());
        });
        subjectsColumn.setCellValueFactory(cellData -> {
            return new SimpleIntegerProperty(cellData.getValue().getSubjects());
        });
        dateAddedColumn.setCellValueFactory(cellData -> {
            Instant startTime = cellData.getValue().getDateAdded();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd").withZone(ZoneId.systemDefault());
            return new SimpleStringProperty(formatter.format(startTime));
        });
        setupActionsColumn();
    }

    private void setupActionsColumn()   {
        actionsColumn.setCellValueFactory(cellData ->   {
            return new SimpleIntegerProperty(cellData.getValue().getStudentId());
        });
        actionsColumn.setCellFactory(tableColumn -> {
            TableCell<Student, Number> cell = new TableCell<>();
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
