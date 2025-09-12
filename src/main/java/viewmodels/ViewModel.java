package viewmodels;

import models.Model;

public interface ViewModel<T> {

    public void setModel(Model<T> model);

}
