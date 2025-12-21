package io.github.unchangingconstant.studenttracker.app.controllers.eventhandlers;

import java.beans.EventHandler;

public class QRCodeEventFilter extends EventHandler {

    //
    private final Character[] scanBuffer = new Character[6]; 

    public QRCodeEventFilter(Object target, String action, String eventPropertyName, String listenerMethodName) {
        super(target, action, eventPropertyName, listenerMethodName);
        
    }
    
    

}
