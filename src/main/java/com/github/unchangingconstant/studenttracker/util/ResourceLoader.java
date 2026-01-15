package com.github.unchangingconstant.studenttracker.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourceLoader {

    // TODO OMG READ section 1.14.3 OF JDBI DOCS!!!!
    public static String loadResource(String resourcePath) {
        try (InputStream inputStream = ResourceLoader.class
                .getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
