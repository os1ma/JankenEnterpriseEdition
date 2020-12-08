package com.example.janken.infrastructure.mysqldao;

import com.example.janken.domain.dao.JankenDetailDao;
import com.example.janken.domain.model.Hand;
import com.example.janken.domain.model.JankenDetail;
import com.example.janken.domain.model.Result;
import lombok.val;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JankenDetailMySQLDao implements JankenDetailDao {

    private static final String SELECT_WHERE_ID_EQUALS_QUERY = "SELECT id, janken_id, player_id, hand, result " +
            "FROM janken_details WHERE id = ?";
    private static final String COUNT_QUERY = "SELECT COUNT(*) FROM janken_details";
    private static final String INSERT_COMMAND = "INSERT INTO janken_details (janken_id, player_id, hand, result) VALUES ";
    private static final String INSERT_COMMAND_VALUE_CLAUSE = "(?, ?, ?, ?)";

    @Override
    public Optional<JankenDetail> findById(long id) {
        try (val conn = DriverManager.getConnection(MySQLDaoConfig.MYSQL_URL,
                MySQLDaoConfig.MYSQL_USER, MySQLDaoConfig.MYSQL_PASSWORD);
             val stmt = conn.prepareStatement(SELECT_WHERE_ID_EQUALS_QUERY)) {

            stmt.setLong(1, id);

            try (val rs = stmt.executeQuery()) {
                return resultSet2JankenDetails(rs).stream().findFirst();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count() {
        try (val conn = DriverManager.getConnection(MySQLDaoConfig.MYSQL_URL,
                MySQLDaoConfig.MYSQL_USER, MySQLDaoConfig.MYSQL_PASSWORD);
             val stmt = conn.prepareStatement(COUNT_QUERY)) {

            try (val rs = stmt.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<JankenDetail> insertAll(List<JankenDetail> jankenDetails) {
        if (jankenDetails.isEmpty()) {
            return new ArrayList<>();
        }

        val command = INSERT_COMMAND + jankenDetails.stream()
                .map(jd -> INSERT_COMMAND_VALUE_CLAUSE)
                .reduce((l, r) -> l + "," + r)
                .get();

        try (val conn = DriverManager.getConnection(MySQLDaoConfig.MYSQL_URL,
                MySQLDaoConfig.MYSQL_USER, MySQLDaoConfig.MYSQL_PASSWORD);
             val stmt = conn.prepareStatement(command, Statement.RETURN_GENERATED_KEYS)) {

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
