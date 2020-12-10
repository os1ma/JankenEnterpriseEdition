package com.example.janken.infrastructure.mysqldao;

import com.example.janken.domain.dao.JankenDao;
import com.example.janken.domain.model.Janken;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.infrastructure.jdbctransaction.JDBCTransaction;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JankenMySQLDao implements JankenDao {

    private static final String SELECT_FROM_CALUSE = "SELECT id, playedAt FROM jankens ";

    private static final String SELECT_ALL_ORDER_BY_ID_QUERY = SELECT_FROM_CALUSE + "ORDER BY id";
    private static final String SELECT_WHERE_ID_EQUALS_QUERY = SELECT_FROM_CALUSE + "WHERE id = ?";

    private static final String COUNT_QUERY = "SELECT COUNT(*) FROM jankens";

    private static final String INSERT_COMMAND = "INSERT INTO jankens (playedAt) VALUES (?)";

    @Override
    public List<Janken> findAllOrderById(Transaction tx) {
        val conn = ((JDBCTransaction) tx).getConn();

        try (val stmt = conn.prepareStatement(SELECT_ALL_ORDER_BY_ID_QUERY)) {

            try (val rs = stmt.executeQuery()) {
                return resultSet2Jankens(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Janken> findById(Transaction tx, long id) {
        val conn = ((JDBCTransaction) tx).getConn();

        try (val stmt = conn.prepareStatement(SELECT_WHERE_ID_EQUALS_QUERY)) {

            stmt.setLong(1, id);

            try (val rs = stmt.executeQuery()) {
                return resultSet2Jankens(rs).stream().findFirst();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count(Transaction tx) {
        val conn = ((JDBCTransaction) tx).getConn();

        try (val stmt = conn.prepareStatement(COUNT_QUERY)) {

            try (val rs = stmt.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Janken insert(Transaction tx, Janken janken) {
        val conn = ((JDBCTransaction) tx).getConn();

        try (val stmt = conn.prepareStatement(INSERT_COMMAND, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setTimestamp(1, Timestamp.valueOf(janken.getPlayedAt()));

            stmt.executeUpdate();

            try (val rs = stmt.getGeneratedKeys()) {
                rs.next();
                val id = rs.getLong(1);

                return new Janken(id, janken.getPlayedAt());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Janken> resultSet2Jankens(ResultSet rs) throws SQLException {
        val list = new ArrayList<Janken>();
        while (rs.next()) {
            val janken = resultSet2Janken(rs);
            list.add(janken);
        }
        return list;
    }

    private Janken resultSet2Janken(ResultSet rs) throws SQLException {
        val id = rs.getLong(1);
        val playedAt = rs.getTimestamp(2).toLocalDateTime();

        return new Janken(id, playedAt);
    }

}
