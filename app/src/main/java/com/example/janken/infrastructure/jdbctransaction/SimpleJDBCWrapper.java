package com.example.janken.infrastructure.jdbctransaction;

import lombok.val;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class SimpleJDBCWrapper {

    public <T> List<T> findList(JDBCTransaction tx,
                                RowMapper<T> mapper,
                                String sql,
                                Object... params) {

        val conn = tx.getConn();

        try (val stmt = conn.prepareStatement(sql)) {

            setParams(stmt, params);

            try (val rs = stmt.executeQuery()) {
                return resultSet2Objects(rs, mapper);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Optional<T> findFirst(JDBCTransaction tx,
                                     RowMapper<T> mapper,
                                     String sql,
                                     Object... params) {

        return findList(tx, mapper, sql, params)
                .stream()
                .findFirst();
    }

    public long count(JDBCTransaction tx,
                      String tableName) {

        val sql = "SELECT COUNT(*) FROM " + tableName;
        val mapper = new SingleRowMapper<Long>();

        return findList(tx, mapper, sql).get(0);
    }

    public <T> void insertAll(JDBCTransaction tx,
                              InsertMapper<T> mapper,
                              String tableName,
                              List<T> objects) {

        if (objects.isEmpty()) {
            return;
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

        val conn = tx.getConn();

        try (val stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setParams(stmt, params);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void insertOne(JDBCTransaction tx,
                              InsertMapper<T> mapper,
                              String tableName,
                              T object) {

        insertAll(tx, mapper, tableName, List.of(object));
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
