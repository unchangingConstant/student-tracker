package io.github.unchangingconstant.studenttracker.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ScriptLoader {

    public static String loadSqlScript(String resourcePath) {
        try (InputStream inputStream = ScriptLoader.class
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
