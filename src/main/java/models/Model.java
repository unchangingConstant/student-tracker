package models;

import javafx.beans.property.ObjectProperty;

/**
 * Provides a means for ViewModels to observe it.
 */
public interface Model<T> {

    public void bind(ObjectProperty<T> property);

}
