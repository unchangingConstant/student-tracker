package io.github.unchangingconstant.studenttracker.app.viewmodels;

import io.github.unchangingconstant.studenttracker.app.models.Model;

public interface ViewModel<T extends Model<?>> {

    // think about this, very rigid. We really only gonna do one model per
    // ViewModel?
    public void setModel(T model);

}
