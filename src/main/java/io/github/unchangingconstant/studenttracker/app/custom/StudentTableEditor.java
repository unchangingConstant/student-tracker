package io.github.unchangingconstant.studenttracker.app.custom;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import io.github.unchangingconstant.studenttracker.app.Controller;
import io.github.unchangingconstant.studenttracker.app.CustomComponentUtils;
import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StudentTableEditor extends TableView<StudentModel> implements Controller {
    
    @FXML
    private TableColumn<StudentModel, Integer> studentIdColumn;
    @FXML
    private TableColumn<StudentModel, String> fullLegalNameColumn;
    @FXML
    private TableColumn<StudentModel, String> prefNameColumn;
    @FXML
    private TableColumn<StudentModel, Integer> subjectsColumn;
    @FXML
    private TableColumn<StudentModel, String> dateAddedColumn;
    @FXML
    private TableColumn<StudentModel, Integer> actionsColumn;

    /*
     * This property being null means that no student has been selected for editing yet
     */
    private final SimpleObjectProperty<StudentModel> currentEditedStudent = new SimpleObjectProperty<>(null);
    public SimpleObjectProperty<StudentModel> currentEditedStudentProperty() {return currentEditedStudent;}

    private Consumer<Integer> onEditAction;
    public void setOnEditAction(Consumer<Integer> onEditAction) {this.onEditAction = onEditAction;}

    private Consumer<Integer> onDeleteAction;
    public void setOnDeleteAction(Consumer<Integer> onDeleteAction)   {this.onDeleteAction = onDeleteAction;}

    /*
     * The disabled property of all Action ComboBoxes in this table is bound to this
     */
    private SimpleBooleanProperty actionsEnabled = new SimpleBooleanProperty(true);
    public SimpleBooleanProperty actionsEnabledProperty() {return actionsEnabled;}


    public StudentTableEditor()   {
        super();
        CustomComponentUtils.hookIntoFXML(this, "/view/components/student_table_view.fxml");
    }

    @Override
    public void initialize() {
        studentIdColumn.setCellValueFactory(cellData -> {
            return cellData.getValue().getStudentId();
        });
        fullLegalNameColumn.setCellValueFactory(cellData -> {
            return cellData.getValue().getFullLegalName();
        });
        prefNameColumn.setCellValueFactory(cellData -> {
            return cellData.getValue().getPrefName();
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
        // Actions buttons will be disabled when table is editing a student
        actionsEnabled.bind(currentEditedStudent.isNull());
    }

    // TODO fix bug. Reproduce by adding two students, and deleting the top-most one from the table
    // The actions button will say Delete on the student that takes its place for some reason
    // TODO should render a "Save" button if current student is being edited. Also, fix above.
    private void setupActionsColumn()   {
        actionsColumn.setCellValueFactory(cellData ->   {
            return cellData.getValue().getStudentId();
        });
        actionsColumn.setCellFactory(tableColumn -> {
            TableCell<StudentModel, Integer> cell = new TableCell<>();
            ComboBox<String> actionsMenu = new ComboBox<>();
            actionsMenu.disableProperty().bind(actionsEnabled.not());
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

    // Makes this component un-focusable
    @Override
    public void requestFocus()  {}

}
