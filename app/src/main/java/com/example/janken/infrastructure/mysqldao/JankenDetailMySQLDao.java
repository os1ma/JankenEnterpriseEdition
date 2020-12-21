package com.example.janken.infrastructure.mysqldao;

import com.example.janken.domain.model.janken.Hand;
import com.example.janken.domain.model.janken.JankenDetail;
import com.example.janken.domain.model.janken.Result;
import com.example.janken.infrastructure.dao.JankenDetailDao;
import com.example.janken.infrastructure.jdbctransaction.InsertMapper;
import com.example.janken.infrastructure.jdbctransaction.JDBCTransaction;
import com.example.janken.infrastructure.jdbctransaction.RowMapper;
import com.example.janken.infrastructure.jdbctransaction.SimpleJDBCWrapper;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JankenDetailMySQLDao implements JankenDetailDao<JDBCTransaction> {

    private static final String TABLE_NAME = "janken_details";

    private static final String SELECT_FROM_CLAUSE = "SELECT id, janken_id, player_id, hand, result " +
            "FROM " + TABLE_NAME + " ";

    private SimpleJDBCWrapper simpleJDBCWrapper = new SimpleJDBCWrapper();
    private JankenDetailRowMapper rowMapper = new JankenDetailRowMapper();
    private JankenDetailInsertMapper insertMapper = new JankenDetailInsertMapper();

    @Override
    public List<JankenDetail> findAllOrderById(JDBCTransaction tx) {
        val sql = SELECT_FROM_CLAUSE + "ORDER BY id";
        return simpleJDBCWrapper.findList(tx, rowMapper, sql);
    }

    @Override
    public List<JankenDetail> findByJankenIdOrderById(JDBCTransaction tx, String jankenId) {
        val sql = SELECT_FROM_CLAUSE + "WHERE janken_id = ? ORDER BY ID";
        return simpleJDBCWrapper.findList(tx, rowMapper, sql, jankenId);
    }

    @Override
    public Optional<JankenDetail> findById(JDBCTransaction tx, String id) {
        val sql = SELECT_FROM_CLAUSE + "WHERE id = ?";
        return simpleJDBCWrapper.findFirst(tx, rowMapper, sql, id);
    }

    @Override
    public long count(JDBCTransaction tx) {
        return simpleJDBCWrapper.count(tx, TABLE_NAME);
    }

    @Override
    public void insertAll(JDBCTransaction tx, List<JankenDetail> jankenDetails) {
        simpleJDBCWrapper.insertAll(tx, insertMapper, TABLE_NAME, jankenDetails);
    }

}

class JankenDetailRowMapper implements RowMapper<JankenDetail> {

    @Override
    public JankenDetail map(ResultSet rs) throws SQLException {
        val id = rs.getString(1);
        val jankenId = rs.getString(2);
        val playerId = rs.getString(3);
        val hand = Hand.of(rs.getInt(4));
        val result = Result.of(rs.getInt(5));

        return new JankenDetail(id, jankenId, playerId, hand, result);
    }

}

class JankenDetailInsertMapper implements InsertMapper<JankenDetail> {

    @Override
    public Map<String, Object> object2InsertParams(JankenDetail object) {
        return Map.of(
                "id", object.getId(),
                "janken_id", object.getJankenId(),
                "player_id", object.getPlayerId(),
                "hand", object.getHand().getValue(),
                "result", object.getResult().getValue());
    }

}
