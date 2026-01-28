package com.github.unchangingconstant.studenttracker.util;

import org.jdbi.v3.core.locator.ClasspathSqlLocator;

public class ResourceLoader {

    // TODO OMG READ section 1.14.3 OF JDBI DOCS!!!!
    public static String loadSQL(String resourcePath) {
        return ClasspathSqlLocator.removingComments().locate("/sql" + resourcePath);
    }

}
