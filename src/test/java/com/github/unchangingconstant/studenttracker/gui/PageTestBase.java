package com.github.unchangingconstant.studenttracker.gui;

import com.google.inject.Injector;
import javafx.stage.Stage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import com.github.unchangingconstant.studenttracker.gui.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class PageTestBase extends ApplicationTest {

    @Mock
    private Injector controllerFactory;

    private WindowManager winMan;

    @Override
    public void start(Stage stage) {
        when(controllerFactory.getInstance(any(Class.class))).thenReturn(buildComponent());
    }

    protected abstract Controller buildComponent();

}
