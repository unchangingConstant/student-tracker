package com.github.unchangingconstant.studenttracker.gui.pages;

import com.github.unchangingconstant.studenttracker.StudentTrackerApp;
import com.github.unchangingconstant.studenttracker.app.dbmanager.DatabaseManager;
import com.github.unchangingconstant.studenttracker.gui.WindowManager;
import com.github.unchangingconstant.studenttracker.gui.models.LiveAttendanceDashboardModel;
import com.github.unchangingconstant.studenttracker.gui.models.StudentTableModel;
import com.google.inject.*;
import com.google.inject.Module;
import com.google.inject.spi.Element;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.TypeConverterBinding;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

@ExtendWith(MockitoExtension.class)
public class AttendanceDashboardPageTest extends ApplicationTest {

    @Mock
    private LiveAttendanceDashboardModel dashModel;
    @Mock
    private StudentTableModel tableModel;
    @Mock
    private DatabaseManager dbMan;
    @Mock
    private Executor exec;
    @Mock
    private WindowManager winMan;

    private AttendanceDashboardPageController page;

    @Override
    public void start(Stage stage) {
        page = new AttendanceDashboardPageController(dashModel, tableModel, dbMan, winMan, exec);

        Scene scene = new Scene(page);
        scene.getStylesheets().add("/view/global.css");
        stage.setScene(scene);

        stage.setTitle(StudentTrackerApp.TITLE);
        stage.setMaximized(true);
        stage.show();
    }

    @BeforeEach
    void setup() {
    }

    @Test
    @DisplayName("Smoke test")
    void testFX() {
        verifyThat("#.startVisitButton", hasText("Start Visit"));
    }



}
