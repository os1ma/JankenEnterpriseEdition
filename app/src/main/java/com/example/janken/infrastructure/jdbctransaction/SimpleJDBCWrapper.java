package com.example.janken.infrastructure.jdbctransaction;

import com.example.janken.domain.transaction.Transaction;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleJDBCWrapper {

    public <T> List<T> findList(Transaction tx,
                                RowMapper<T> mapper,
                                String sql,
                                Object... params) {

        val conn = ((JDBCTransaction) tx).getConn();

        try (val stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (val rs = stmt.executeQuery()) {
                return resultSet2Objects(rs, mapper);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Optional<T> findFirst(Transaction tx,
                                     RowMapper<T> mapper,
                                     String sql,
                                     Object... params) {
        return findList(tx, mapper, sql, params)
                .stream()
                .findFirst();
    }

    public long count(Transaction tx, String tableName) {
        val sql = "SELECT COUNT(*) FROM " + tableName;
        val mapper = new SingleRowMapper<Long>();

        return findList(tx, mapper, sql).get(0);
    }

    private <T> List<T> resultSet2Objects(ResultSet rs, RowMapper<T> mapper) throws SQLException {
        val list = new ArrayList<T>();
        while (rs.next()) {
            val obj = mapper.map(rs);
            list.add(obj);
        }
        return list;
    }

}
