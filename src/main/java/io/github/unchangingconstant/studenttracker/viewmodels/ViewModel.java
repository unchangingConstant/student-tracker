package io.github.unchangingconstant.studenttracker.viewmodels;

import io.github.unchangingconstant.studenttracker.models.Model;

public interface ViewModel<T extends Model<?>> {

    public void setModel(T model);

}
