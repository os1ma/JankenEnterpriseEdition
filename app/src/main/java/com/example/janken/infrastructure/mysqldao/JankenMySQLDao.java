package com.example.janken.infrastructure.mysqldao;

import com.example.janken.infrastructure.dao.JankenDao;
import com.example.janken.domain.model.janken.Janken;
import com.example.janken.domain.model.janken.JankenDetail;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.infrastructure.jdbctransaction.InsertMapper;
import com.example.janken.infrastructure.jdbctransaction.RowMapper;
import com.example.janken.infrastructure.jdbctransaction.SimpleJDBCWrapper;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JankenMySQLDao implements JankenDao {

    private static final String TABLE_NAME = "jankens";

    private static final String SELECT_FROM_CALUSE = "SELECT id, played_at FROM " + TABLE_NAME + " ";

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
        return simpleJDBCWrapper.count(tx, TABLE_NAME);
    }

    @Override
    public Janken insert(Transaction tx, Janken janken) {
        return simpleJDBCWrapper.insertOneAndReturnObjectWithKey(tx, insertMapper, TABLE_NAME, janken);
    }

}

class JankenRowMapper implements RowMapper<Janken> {

    @Override
    public Janken map(ResultSet rs) throws SQLException {
        val id = rs.getLong(1);
        val playedAt = rs.getTimestamp(2).toLocalDateTime();

        return new Janken(id, playedAt, null, null);
    }

}

class JankenInsertMapper implements InsertMapper<Janken> {

    @Override
    public Map<String, Object> object2InsertParams(Janken object) {
        return Map.of("played_at", Timestamp.valueOf(object.getPlayedAt()));
    }

    @Override
    public Janken zipWithKey(long key, Janken objectWithoutKey) {
        val detail1 = objectWithoutKey.getDetail1();
        val detail1WithId = new JankenDetail(
                null,
                key,
                detail1.getPlayerId(),
                detail1.getHand(),
                detail1.getResult());

        val details2 = objectWithoutKey.getDetail2();
        val detail2WithId = new JankenDetail(
                null,
                key,
                details2.getPlayerId(),
                details2.getHand(),
                details2.getResult());

        return new Janken(
                key,
                objectWithoutKey.getPlayedAt(),
                detail1WithId,
                detail2WithId);
    }

}

