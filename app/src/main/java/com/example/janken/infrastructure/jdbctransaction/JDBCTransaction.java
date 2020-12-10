package com.example.janken.infrastructure.jdbctransaction;

import com.example.janken.framework.Transaction;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class JDBCTransaction implements Transaction {

    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/janken";
    private static final String MYSQL_USER = "user";
    private static final String MYSQL_PASSWORD = "password";

    private Connection conn;

    public JDBCTransaction() {
        try {
            this.conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
            conn.setAutoCommit(false);

        } catch (SQLException e) {
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
