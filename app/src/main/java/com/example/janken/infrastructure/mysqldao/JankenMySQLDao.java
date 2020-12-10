package com.example.janken.infrastructure.mysqldao;

import com.example.janken.domain.dao.JankenDao;
import com.example.janken.domain.model.Janken;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.infrastructure.jdbctransaction.InsertMapper;
import com.example.janken.infrastructure.jdbctransaction.RowMapper;
import com.example.janken.infrastructure.jdbctransaction.SimpleJDBCWrapper;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class JankenMySQLDao implements JankenDao {

    private static final String SELECT_FROM_CALUSE = "SELECT id, playedAt FROM jankens ";
    private static final String INSERT_COMMAND = "INSERT INTO jankens (playedAt) VALUES (?)";

    private SimpleJDBCWrapper simpleJDBCWrapper = new SimpleJDBCWrapper();
    private JankenRowMapper rowMapper = new JankenRowMapper();
    private JankenInsertMapper insertMapper = new JankenInsertMapper();

    @Override
    public List<Janken> findAllOrderById(Transaction tx) {
        val sql = SELECT_FROM_CALUSE + "ORDER BY id";
        return simpleJDBCWrapper.findList(tx, rowMapper, sql);
    }

    @Override
    public Optional<Janken> findById(Transaction tx, long id) {
        val sql = SELECT_FROM_CALUSE + "WHERE id = ?";
        return simpleJDBCWrapper.findFirst(tx, rowMapper, sql, id);
    }

    @Override
    public long count(Transaction tx) {
        return simpleJDBCWrapper.count(tx, "jankens");
    }

    @Override
    public Janken insert(Transaction tx, Janken janken) {
        return simpleJDBCWrapper.insertOneAndReturnWithKey(tx, insertMapper, INSERT_COMMAND, janken);
    }

}

class JankenRowMapper implements RowMapper<Janken> {

    @Override
    public Janken map(ResultSet rs) throws SQLException {
        val id = rs.getLong(1);
        val playedAt = rs.getTimestamp(2).toLocalDateTime();

        return new Janken(id, playedAt);
    }

}

class JankenInsertMapper implements InsertMapper<Janken> {

    @Override
    public List<Object> object2InsertParams(Janken object) {
        return List.of(Timestamp.valueOf(object.getPlayedAt()));
    }

    @Override
    public Janken zipWithKey(long key, Janken objectWithoutKey) {
        return new Janken(key, objectWithoutKey.getPlayedAt());
    }

}

