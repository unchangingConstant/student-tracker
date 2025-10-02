package io.github.unchangingconstant.studenttracker.context;

import io.github.unchangingconstant.studenttracker.app.models.Model;

public class ModelProvider {

    protected <T extends Model<?>> T locate(Class<T> model) {
        return null;
    }

}
