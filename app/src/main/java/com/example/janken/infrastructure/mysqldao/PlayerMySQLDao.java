package com.example.janken.infrastructure.mysqldao;

import com.example.janken.domain.dao.PlayerDao;
import com.example.janken.domain.model.Player;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.infrastructure.jdbctransaction.JDBCTransaction;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerMySQLDao implements PlayerDao {

    private static final String SELECT_WHERE_ID_EQUALS_QUERY = "SELECT id, name FROM players WHERE id = ?";

    @Override
    public Player findPlayerById(Transaction tx, long playerId) {
        val conn = ((JDBCTransaction) tx).getConn();

        try (val stmt = conn.prepareStatement(SELECT_WHERE_ID_EQUALS_QUERY)) {

            stmt.setLong(1, playerId);

            try (val rs = stmt.executeQuery()) {
                return resultSet2Players(rs).get(0);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Player> resultSet2Players(ResultSet rs) throws SQLException {
        val list = new ArrayList<Player>();
        while (rs.next()) {
            val player = resultSet2Player(rs);
            list.add(player);
        }
        return list;
    }

    private Player resultSet2Player(ResultSet rs) throws SQLException {
        val id = rs.getLong(1);
        val name = rs.getString(2);

        return new Player(id, name);
    }

}
