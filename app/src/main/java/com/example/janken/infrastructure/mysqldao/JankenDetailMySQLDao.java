package com.example.janken.infrastructure.mysqldao;

import com.example.janken.domain.dao.JankenDetailDao;
import com.example.janken.domain.model.Hand;
import com.example.janken.domain.model.JankenDetail;
import com.example.janken.domain.model.Result;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.infrastructure.jdbctransaction.InsertMapper;
import com.example.janken.infrastructure.jdbctransaction.RowMapper;
import com.example.janken.infrastructure.jdbctransaction.SimpleJDBCWrapper;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JankenDetailMySQLDao implements JankenDetailDao {

    private static final String SELECT_FROM_CLAUSE = "SELECT id, janken_id, player_id, hand, result FROM janken_details ";
    private static final String INSERT_COMMAND = "INSERT INTO janken_details (janken_id, player_id, hand, result) VALUES ";
    private static final String INSERT_COMMAND_VALUE_CLAUSE = "(?, ?, ?, ?)";

    private SimpleJDBCWrapper simpleJDBCWrapper = new SimpleJDBCWrapper();
    private JankenDetailRowMapper rowMapper = new JankenDetailRowMapper();
    private JankenDetailInsertMapper insertMapper = new JankenDetailInsertMapper();

    @Override
    public List<JankenDetail> findAllOrderById(Transaction tx) {
        val sql = SELECT_FROM_CLAUSE + "ORDER BY id";
        return simpleJDBCWrapper.findList(tx, rowMapper, sql);
    }

    @Override
    public Optional<JankenDetail> findById(Transaction tx, long id) {
        val sql = SELECT_FROM_CLAUSE + "WHERE id = ?";
        return simpleJDBCWrapper.findFirst(tx, rowMapper, sql, id);
    }

    @Override
    public long count(Transaction tx) {
        return simpleJDBCWrapper.count(tx, "janken_details");
    }

    @Override
    public List<JankenDetail> insertAll(Transaction tx, List<JankenDetail> jankenDetails) {
        if (jankenDetails.isEmpty()) {
            return new ArrayList<>();
        }

        val sql = INSERT_COMMAND + jankenDetails.stream()
                .map(jd -> INSERT_COMMAND_VALUE_CLAUSE)
                .collect(Collectors.joining(", "));

        return simpleJDBCWrapper.insertAndReturnWithKeys(tx, insertMapper, sql, jankenDetails);
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
    public List<Object> object2InsertParams(JankenDetail object) {
        return List.of(
                object.getJankenId(),
                object.getPlayerId(),
                object.getHand().getValue(),
                object.getResult().getValue());
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
