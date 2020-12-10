package com.example.janken.infrastructure.mysqldao;

import com.example.janken.domain.dao.JankenDetailDao;
import com.example.janken.domain.model.Hand;
import com.example.janken.domain.model.JankenDetail;
import com.example.janken.domain.model.Result;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.infrastructure.jdbctransaction.RowMapper;
import com.example.janken.infrastructure.jdbctransaction.SimpleJDBCWrapper;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JankenDetailMySQLDao implements JankenDetailDao {

    private static final String SELECT_FROM_CLAUSE = "SELECT id, janken_id, player_id, hand, result FROM janken_details ";
    private static final String INSERT_COMMAND = "INSERT INTO janken_details (janken_id, player_id, hand, result) VALUES ";
    private static final String INSERT_COMMAND_VALUE_CLAUSE = "(?, ?, ?, ?)";

    private SimpleJDBCWrapper simpleJDBCWrapper = new SimpleJDBCWrapper();
    private JankenDetailMapper mapper = new JankenDetailMapper();

    @Override
    public List<JankenDetail> findAllOrderById(Transaction tx) {
        val sql = SELECT_FROM_CLAUSE + "ORDER BY id";
        return simpleJDBCWrapper.findList(tx, mapper, sql);
    }

    @Override
    public Optional<JankenDetail> findById(Transaction tx, long id) {
        val sql = SELECT_FROM_CLAUSE + "WHERE id = ?";
        return simpleJDBCWrapper.findFirst(tx, mapper, sql, id);
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
                .reduce((l, r) -> l + "," + r)
                .get();

        val params = new ArrayList<Object>();
        for (val jd : jankenDetails) {
            params.add(jd.getJankenId());
            params.add(jd.getPlayerId());
            params.add(jd.getHand().getValue());
            params.add(jd.getResult().getValue());
        }

        val ids = simpleJDBCWrapper.insertAndReturnKeys(tx, sql, params.toArray());

        val jankenDetailWithIds = new ArrayList<JankenDetail>();
        for (int i = 0; i < jankenDetails.size(); i++) {
            val jd = jankenDetails.get(i);
            val id = ids.get(i);

            val jankenDetailWithId = new JankenDetail(id, jd.getJankenId(), jd.getPlayerId(), jd.getHand(), jd.getResult());
            jankenDetailWithIds.add(jankenDetailWithId);
        }
        return jankenDetailWithIds;
    }

}

class JankenDetailMapper implements RowMapper<JankenDetail> {

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
