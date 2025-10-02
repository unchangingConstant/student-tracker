package io.github.unchangingconstant.studenttracker.context;

import io.github.unchangingconstant.studenttracker.app.models.Model;

/**
 * This class has the following goals:
 * 
 * - Handle dependency injection
 * - Manage app component lifecycles
 * - Make all app components visible to any part of the app
 */
public class AppContext {

    private AppContext() {
        throw new UnsupportedOperationException("Singleton object");
    }

    public Model<?> getModel() {
        return null;
    }

}
