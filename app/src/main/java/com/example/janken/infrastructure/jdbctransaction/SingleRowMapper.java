package com.example.janken.infrastructure.jdbctransaction;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SingleRowMapper<T> implements RowMapper<T> {

    @SuppressWarnings("unchecked")
    @Override
    public T map(ResultSet rs) throws SQLException {
        return (T) rs.getObject(1);
    }

}
