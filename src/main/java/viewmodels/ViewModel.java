package viewmodels;

import models.Model;

public interface ViewModel<T extends Model<?>> {

    public void setModel(T model);

}
