package io.github.unchangingconstant.studenttracker.models;

import javafx.beans.property.Property;

/**
 * The model encapsulates a single observable value object that can only be
 * directly observed via the bind() method. The generic type should specify the
 * kinds of properties able to observe the model.
 * 
 * Means of modifying the value should be defined in this class' implementation
 */
public interface Model<T extends Property<?>> {

    public void bind(T property);

}
