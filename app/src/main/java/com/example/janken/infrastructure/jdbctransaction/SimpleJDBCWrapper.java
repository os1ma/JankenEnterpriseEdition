package com.example.janken.infrastructure.jdbctransaction;

import com.example.janken.domain.transaction.Transaction;
import lombok.val;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SimpleJDBCWrapper {

    public <T> List<T> findList(Transaction tx,
                                RowMapper<T> mapper,
                                String sql,
                                Object... params) {

        val conn = ((JDBCTransaction) tx).getConn();

        try (val stmt = conn.prepareStatement(sql)) {

            setParams(stmt, params);

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

    public long count(Transaction tx,
                      String tableName) {

        val sql = "SELECT COUNT(*) FROM " + tableName;
        val mapper = new SingleRowMapper<Long>();

        return findList(tx, mapper, sql).get(0);
    }

    public <T> List<T> insertAndReturnWithKeys(Transaction tx,
                                               InsertMapper<T> mapper,
                                               String sql,
                                               List<T> objects) {

        val keys = insertAndReturnKeys(tx, mapper, sql, objects);

        val objectsWithKeys = new ArrayList<T>();
        for (int i = 0; i < objects.size(); i++) {
            val obj = objects.get(i);
            val key = keys.get(i);

            val objectsWithKey = mapper.zipWithKey(key, obj);
            objectsWithKeys.add(objectsWithKey);
        }
        return objectsWithKeys;
    }

    public <T> T insertOneAndReturnWithKey(Transaction tx,
                                           InsertMapper<T> mapper,
                                           String sql,
                                           T object) {
        return insertAndReturnWithKeys(tx, mapper, sql, List.of(object)).get(0);
    }

    // private methods

    private <T> List<Long> insertAndReturnKeys(Transaction tx,
                                               InsertMapper<T> mapper,
                                               String sql,
                                               List<T> objects) {

        val conn = ((JDBCTransaction) tx).getConn();

        val params = objects.stream()
                .map(mapper::object2InsertParams)
                .flatMap(List::stream)
                .toArray();

        try (val stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setParams(stmt, params);

            stmt.executeUpdate();

            try (val rs = stmt.getGeneratedKeys()) {
                val idMapper = new SingleRowMapper<BigInteger>();
                return resultSet2Objects(rs, idMapper).stream()
                        .map(BigInteger::longValue)
                        .collect(Collectors.toList());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setParams(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
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