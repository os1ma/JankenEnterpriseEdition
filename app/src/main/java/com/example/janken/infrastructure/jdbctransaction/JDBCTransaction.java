package com.example.janken.infrastructure.jdbctransaction;

import com.example.janken.domain.transaction.Transaction;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class JDBCTransaction implements Transaction {

    private static final String DEFAULT_MYSQL_HOST = "localhost";
    private static final String DEFAULT_MYSQL_DATABASE = "janken";
    private static final String DEFAULT_MYSQL_USER = "user";
    private static final String DEFAULT_MYSQL_PASSWORD = "password";

    private static final String MYSQL_HOST_ENV_VARIABLE = System.getenv("MYSQL_HOST");
    private static final String MYSQL_DATABASE_ENV_VARIABLE = System.getenv("MYSQL_DATABASE");
    private static final String MYSQL_USER_ENV_VARIABLE = System.getenv("MYSQL_USER");
    private static final String MYSQL_PASSWORD_ENV_VARIABLE = System.getenv("MYSQL_PASSWORD");

    private static final String MYSQL_HOST = MYSQL_HOST_ENV_VARIABLE != null
            ? MYSQL_HOST_ENV_VARIABLE
            : DEFAULT_MYSQL_HOST;
    private static final String MYSQL_DATABASE = MYSQL_DATABASE_ENV_VARIABLE != null
            ? MYSQL_DATABASE_ENV_VARIABLE
            : DEFAULT_MYSQL_DATABASE;
    private static final String MYSQL_USER = MYSQL_USER_ENV_VARIABLE != null
            ? MYSQL_USER_ENV_VARIABLE
            : DEFAULT_MYSQL_USER;
    private static final String MYSQL_PASSWORD = MYSQL_PASSWORD_ENV_VARIABLE != null
            ? MYSQL_PASSWORD_ENV_VARIABLE
            : DEFAULT_MYSQL_PASSWORD;

    private static final String MYSQL_URL = "jdbc:mysql://" + MYSQL_HOST + ":3306/" + MYSQL_DATABASE;

    private Connection conn;

    public JDBCTransaction() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            this.conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
            conn.setAutoCommit(false);

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        try {
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
