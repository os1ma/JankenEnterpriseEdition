package com.example.janken.infrastructure.mysqldao;

import com.example.janken.infrastructure.dao.JankenDetailDao;
import com.example.janken.domain.model.janken.Hand;
import com.example.janken.domain.model.janken.JankenDetail;
import com.example.janken.domain.model.janken.Result;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.infrastructure.jdbctransaction.InsertMapper;
import com.example.janken.infrastructure.jdbctransaction.RowMapper;
import com.example.janken.infrastructure.jdbctransaction.SimpleJDBCWrapper;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JankenDetailMySQLDao implements JankenDetailDao {

    private static final String TABLE_NAME = "janken_details";

    private static final String SELECT_FROM_CLAUSE = "SELECT id, janken_id, player_id, hand, result " +
            "FROM " + TABLE_NAME + " ";

    private SimpleJDBCWrapper simpleJDBCWrapper = new SimpleJDBCWrapper();
    private JankenDetailRowMapper rowMapper = new JankenDetailRowMapper();
    private JankenDetailInsertMapper insertMapper = new JankenDetailInsertMapper();

    @Override
    public List<JankenDetail> findAllOrderById(Transaction tx) {
        val sql = SELECT_FROM_CLAUSE + "ORDER BY id";
        return simpleJDBCWrapper.findList(tx, rowMapper, sql);
    }

    @Override
    public List<JankenDetail> findByJankenIdOrderById(Transaction tx, long jankenId) {
        val sql = SELECT_FROM_CLAUSE + "WHERE janken_id = ? ORDER BY ID";
        return simpleJDBCWrapper.findList(tx, rowMapper, sql, jankenId);
    }

    @Override
    public Optional<JankenDetail> findById(Transaction tx, long id) {
        val sql = SELECT_FROM_CLAUSE + "WHERE id = ?";
        return simpleJDBCWrapper.findFirst(tx, rowMapper, sql, id);
    }

    @Override
    public long count(Transaction tx) {
        return simpleJDBCWrapper.count(tx, TABLE_NAME);
    }

    @Override
    public List<JankenDetail> insertAll(Transaction tx, List<JankenDetail> jankenDetails) {
        return simpleJDBCWrapper.insertAndReturnObjectWithKeys(tx, insertMapper, TABLE_NAME, jankenDetails);
    }

}

class JankenDetailRowMapper implements RowMapper<JankenDetail> {

    @Override
    public JankenDetail map(ResultSet rs) throws SQLException {
        val id = rs.getLong(1);
        val jankenId = rs.getLong(2);
        val playerId = rs.getLong(3);
        val hand = Hand.of(rs.getInt(4));
        val result = Result.of(rs.getInt(5));

        return new JankenDetail(id, jankenId, playerId, hand, result);
    }

}

class JankenDetailInsertMapper implements InsertMapper<JankenDetail> {

    @Override
    public Map<String, Object> object2InsertParams(JankenDetail object) {
        return Map.of(
                "janken_id", object.getJankenId(),
                "player_id", object.getPlayerId(),
                "hand", object.getHand().getValue(),
                "result", object.getResult().getValue());
    }

    @Override
    public JankenDetail zipWithKey(long key, JankenDetail objectWithoutKey) {
        return new JankenDetail(
                key,
                objectWithoutKey.getJankenId(),
                objectWithoutKey.getPlayerId(),
                objectWithoutKey.getHand(),
                objectWithoutKey.getResult());
    }

}
