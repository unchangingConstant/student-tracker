package com.github.unchangingconstant.studenttracker.gui.taskutils;

import javafx.concurrent.Task;

/**
 * IDK, I feel like this might be useful someday.
 * 
 * For example, it'd be nice not to have to write all that boiler plate
 * everytime I call a service.'
 * 
 * Another example, a task gives JavaFX an interface for the JavaFX thread
 * to observe to completion status of a task. If this ever gets serious, it'd
 * be useful to implement things like wait messages or status indicators. For 
 * example, including a wait dialog when exporting student records
 */
public abstract class ServiceTask<T> extends Task<T> {
    
    public ServiceTask() {
        super();
    }

}
