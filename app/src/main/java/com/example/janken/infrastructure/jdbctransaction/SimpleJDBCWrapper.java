package com.example.janken.infrastructure.jdbctransaction;

import com.example.janken.domain.transaction.Transaction;
import lombok.val;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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

    public <T> List<T> insertAndReturnObjectWithKeys(Transaction tx,
                                                     InsertMapper<T> mapper,
                                                     String tableName,
                                                     List<T> objects) {

        val keys = insertAndReturnKeys(tx, mapper, tableName, objects);

        // 自動採番されたキーとオブジェクトを紐付けた新たなオブジェクトを作成して返す
        val objectsWithKeys = new ArrayList<T>();
        for (int i = 0; i < objects.size(); i++) {
            val obj = objects.get(i);
            val key = keys.get(i);

            val objectsWithKey = mapper.zipWithKey(key, obj);
            objectsWithKeys.add(objectsWithKey);
        }
        return objectsWithKeys;
    }

    public <T> T insertOneAndReturnObjectWithKey(Transaction tx,
                                                 InsertMapper<T> mapper,
                                                 String tableName,
                                                 T object) {

        return insertAndReturnObjectWithKeys(tx, mapper, tableName, List.of(object)).get(0);
    }

    // find 関係の private method

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

    // insert 関係の private method

    private <T> List<Long> insertAndReturnKeys(Transaction tx,
                                               InsertMapper<T> mapper,
                                               String tableName,
                                               List<T> objects) {

        if (objects.isEmpty()) {
            return new ArrayList<>();
        }

        val columnNames = getSortedColumnNames(mapper, objects);

        val sql = buildInsertSql(tableName, columnNames, objects.size());

        val params = objects.stream()
                .map(mapper::object2InsertParams)
                .flatMap(rowMap -> rowMap.entrySet()
                        .stream()
                        // カラム名の順番でソート
                        .sorted(Comparator.comparingInt(e -> columnNames.indexOf(e.getKey())))
                        .map(Map.Entry::getValue))
                .toArray();

        val conn = ((JDBCTransaction) tx).getConn();

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

    private String buildInsertSql(String tableName,
                                  List<String> columnNames,
                                  long rowCount) {

        // (col1, col2) のような文字列
        val columnNamesStr = "(" + String.join(", ", columnNames) + ")";

        // ?, ? のような文字列
        val insertRowPlaceholders = columnNames
                .stream()
                .map(k -> "?")
                .collect(Collectors.joining(", "));

        // (?, ?), (?, ?) のような文字列
        val insertValues = "(" +
                LongStream.range(0, rowCount)
                        .mapToObj(o -> insertRowPlaceholders)
                        .collect(Collectors.joining("), (")) +
                ")";

        return "INSERT INTO " + tableName + " " + columnNamesStr + " VALUES " + insertValues;
    }

    private <T> List<String> getSortedColumnNames(InsertMapper<T> mapper,
                                                  List<T> objects) {

        return mapper.object2InsertParams(objects.get(0))
                .keySet()
                .stream()
                .sorted()
                .collect(Collectors.toList());
    }

}
