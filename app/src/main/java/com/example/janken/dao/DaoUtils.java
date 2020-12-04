package com.example.janken.dao;

import lombok.val;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

class DaoUtils {

    // JankenEnterpriseEdition/app/../data/ を指す
    private static final String DEFAULT_DATA_DIR = System.getProperty("user.dir") + "/../data/";
    private static final String DATA_DIR_ENV_VARIABLE = System.getenv("DATA_DIR");
    static final String DATA_DIR = DATA_DIR_ENV_VARIABLE != null ? DATA_DIR_ENV_VARIABLE + "/" : DEFAULT_DATA_DIR;
    static final String CSV_DELIMITER = ",";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static long countFileLines(String path) {
        val file = new File(path);

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        try (val stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            return stream.count();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
