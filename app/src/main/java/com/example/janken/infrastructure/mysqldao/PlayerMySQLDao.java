package com.example.janken.infrastructure.mysqldao;

import com.example.janken.domain.dao.PlayerDao;
import com.example.janken.domain.model.Player;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.infrastructure.jdbctransaction.RowMapper;
import com.example.janken.infrastructure.jdbctransaction.SimpleJDBCWrapper;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerMySQLDao implements PlayerDao {

    private static final String SELECT_FROM_CLAUSE = "SELECT id, name FROM players ";

    private SimpleJDBCWrapper simpleJDBCWrapper = new SimpleJDBCWrapper();
    private PlayerMapper mapper = new PlayerMapper();

    @Override
    public Player findPlayerById(Transaction tx, long playerId) {
        val sql = SELECT_FROM_CLAUSE + "WHERE id = ?";
        return simpleJDBCWrapper.findFirst(tx, mapper, sql, playerId).get();
    }

}

class PlayerMapper implements RowMapper<Player> {

    @Override
    public Player map(ResultSet rs) throws SQLException {
        val id = rs.getLong(1);
        val name = rs.getString(2);

        return new Player(id, name);
    }

}
