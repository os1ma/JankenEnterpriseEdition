package com.example.janken.infrastructure.mysqldao;

import com.example.janken.infrastructure.dao.PlayerDao;
import com.example.janken.domain.model.player.Player;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.infrastructure.jdbctransaction.RowMapper;
import com.example.janken.infrastructure.jdbctransaction.SimpleJDBCWrapper;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerMySQLDao implements PlayerDao {

    private static final String SELECT_FROM_CLAUSE = "SELECT id, name FROM players ";

    private SimpleJDBCWrapper simpleJDBCWrapper = new SimpleJDBCWrapper();
    private PlayerRowMapper rowMapper = new PlayerRowMapper();

    @Override
    public Player findPlayerById(Transaction tx, String playerId) {
        val sql = SELECT_FROM_CLAUSE + "WHERE id = ?";
        return simpleJDBCWrapper.findFirst(tx, rowMapper, sql, playerId).get();
    }

}

class PlayerRowMapper implements RowMapper<Player> {

    @Override
    public Player map(ResultSet rs) throws SQLException {
        val id = rs.getString(1);
        val name = rs.getString(2);

        return new Player(id, name);
    }

}
