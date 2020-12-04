package com.example.janken.service;

class ServiceConfigurations {

    // JankenEnterpriseEdition/app/../data/ を指す
    private static final String DEFAULT_DATA_DIR = System.getProperty("user.dir") + "/../data/";
    private static final String DATA_DIR_ENV_VARIABLE = System.getenv("DATA_DIR");
    static final String DATA_DIR = DATA_DIR_ENV_VARIABLE != null ? DATA_DIR_ENV_VARIABLE + "/" : DEFAULT_DATA_DIR;
    static final String CSV_DELIMITER = ",";

}
