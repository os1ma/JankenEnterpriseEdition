package com.example.janken.infrastructure.jdbctransaction;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {

    T map(ResultSet rs) throws SQLException;

}
