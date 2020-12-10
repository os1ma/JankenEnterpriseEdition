package com.example.janken.infrastructure.mysqldao;

import com.example.janken.domain.dao.JankenDetailDao;
import com.example.janken.domain.model.Hand;
import com.example.janken.domain.model.JankenDetail;
import com.example.janken.domain.model.Result;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.infrastructure.jdbctransaction.JDBCTransaction;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JankenDetailMySQLDao implements JankenDetailDao {

    private static final String SELECT_FROM_CLAUSE = "SELECT id, janken_id, player_id, hand, result FROM janken_details ";

    private static final String SELECT_ALL_ORDER_BY_ID_QUERY = SELECT_FROM_CLAUSE + "ORDER BY id";
    private static final String SELECT_WHERE_ID_EQUALS_QUERY = SELECT_FROM_CLAUSE + "WHERE id = ?";

    private static final String COUNT_QUERY = "SELECT COUNT(*) FROM janken_details";

    private static final String INSERT_COMMAND = "INSERT INTO janken_details (janken_id, player_id, hand, result) VALUES ";
    private static final String INSERT_COMMAND_VALUE_CLAUSE = "(?, ?, ?, ?)";

    @Override
    public List<JankenDetail> findAllOrderById(Transaction tx) {
        val conn = ((JDBCTransaction) tx).getConn();

        try (val stmt = conn.prepareStatement(SELECT_ALL_ORDER_BY_ID_QUERY)) {

            try (val rs = stmt.executeQuery()) {
                return resultSet2JankenDetails(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<JankenDetail> findById(Transaction tx, long id) {
        val conn = ((JDBCTransaction) tx).getConn();

        try (val stmt = conn.prepareStatement(SELECT_WHERE_ID_EQUALS_QUERY)) {

            stmt.setLong(1, id);

            try (val rs = stmt.executeQuery()) {
                return resultSet2JankenDetails(rs).stream().findFirst();
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
    public List<JankenDetail> insertAll(Transaction tx, List<JankenDetail> jankenDetails) {
        if (jankenDetails.isEmpty()) {
            return new ArrayList<>();
        }

        val command = INSERT_COMMAND + jankenDetails.stream()
                .map(jd -> INSERT_COMMAND_VALUE_CLAUSE)
                .reduce((l, r) -> l + "," + r)
                .get();

        val conn = ((JDBCTransaction) tx).getConn();

        try (val stmt = conn.prepareStatement(command, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < jankenDetails.size(); i++) {
                val jd = jankenDetails.get(i);
                val placeholderOffset = 4 * i;

                stmt.setLong(placeholderOffset + 1, jd.getJankenId());
                stmt.setLong(placeholderOffset + 2, jd.getPlayerId());
                stmt.setInt(placeholderOffset + 3, jd.getHand().getValue());
                stmt.setInt(placeholderOffset + 4, jd.getResult().getValue());
            }

            stmt.executeUpdate();

            val jankenDetailWithIds = new ArrayList<JankenDetail>();
            try (val rs = stmt.getGeneratedKeys()) {
                for (JankenDetail jd : jankenDetails) {
                    rs.next();
                    val id = rs.getLong(1);

                    val jankenDetailWithId = new JankenDetail(id, jd.getJankenId(), jd.getPlayerId(), jd.getHand(), jd.getResult());
                    jankenDetailWithIds.add(jankenDetailWithId);
                }
            }
            return jankenDetailWithIds;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<JankenDetail> resultSet2JankenDetails(ResultSet rs) throws SQLException {
        val list = new ArrayList<JankenDetail>();
        while (rs.next()) {
            val jankenDetail = resultSet2JankenDetail(rs);
            list.add(jankenDetail);
        }
        return list;
    }

    private JankenDetail resultSet2JankenDetail(ResultSet rs) throws SQLException {
        val id = rs.getLong(1);
        val jankenId = rs.getLong(2);
        val playerId = rs.getLong(3);
        val hand = Hand.of(rs.getInt(4));
        val result = Result.of(rs.getInt(5));

        return new JankenDetail(id, jankenId, playerId, hand, result);
    }

}
