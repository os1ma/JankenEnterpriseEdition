package com.example.janken.application.service;

import com.example.janken.domain.model.janken.Hand;
import com.example.janken.domain.model.janken.JankenDetail;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.infrastructure.dao.JankenDetailDao;
import com.example.janken.infrastructure.jdbctransaction.JDBCTransactionManager;
import com.example.janken.infrastructure.mysqldao.JankenDetailMySQLDao;
import com.example.janken.infrastructure.mysqldao.JankenMySQLDao;
import com.example.janken.infrastructure.mysqldao.PlayerMySQLDao;
import com.example.janken.infrastructure.mysqlrepository.JankenMySQLRepository;
import com.example.janken.infrastructure.mysqlrepository.PlayerMySQLRepository;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JankenApplicationServiceTest {

    @Test
    public void じゃんけん明細保存時に例外が発生した場合じゃんけんも保存されない() {

        // 準備

        val tm = new JDBCTransactionManager();

        val playerDao = new PlayerMySQLDao();
        val jankenDao = new JankenMySQLDao();
        val jankenDetailDao = new JankenDetailErrorDao();

        val playerRepository = new PlayerMySQLRepository(playerDao);
        val jankenRepository = new JankenMySQLRepository(jankenDao, jankenDetailDao);

        val service = new JankenApplicationService(tm, jankenRepository, playerRepository);

        val jankenCountBeforeTest = tm.transactional(jankenDao::count);

        // 実行

        try {
            service.play("1", Hand.STONE, "2", Hand.STONE);

            // 例外が発生しない場合はテスト失敗
            fail();
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }

        // 検証

        val jankenCountAfterTest = tm.transactional(jankenDao::count);
        assertEquals(jankenCountBeforeTest, jankenCountAfterTest, "じゃんけんの件数が増えていない");
    }

}

@NoArgsConstructor
class JankenDetailErrorDao implements JankenDetailDao {

    @Override
    public List<JankenDetail> findAllOrderById(Transaction tx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<JankenDetail> findByJankenIdOrderById(Transaction tx, String jankenId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<JankenDetail> findById(Transaction tx, String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count(Transaction tx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertAll(Transaction tx, List<JankenDetail> jankenDetails) {
        throw new UnsupportedOperationException();
    }

}
