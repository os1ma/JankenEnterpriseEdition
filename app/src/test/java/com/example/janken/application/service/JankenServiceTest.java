package com.example.janken.application.service;

import com.example.janken.domain.dao.JankenDao;
import com.example.janken.domain.dao.JankenDetailDao;
import com.example.janken.domain.model.Hand;
import com.example.janken.domain.model.JankenDetail;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.domain.transaction.TransactionManager;
import com.example.janken.infrastructure.jdbctransaction.JDBCTransactionManager;
import com.example.janken.infrastructure.mysqldao.JankenMySQLDao;
import com.example.janken.registry.ServiceLocator;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JankenServiceTest {

    @Test
    public void じゃんけん明細保存時に例外が発生した場合じゃんけんも保存されない() {

        // 準備

        ServiceLocator.register(TransactionManager.class, JDBCTransactionManager.class);
        ServiceLocator.register(JankenDao.class, JankenMySQLDao.class);
        ServiceLocator.register(JankenDetailDao.class, JankenDetailErrorDao.class);

        val tm = ServiceLocator.resolve(TransactionManager.class);
        val service = new JankenService();
        val jankenDao = ServiceLocator.resolve(JankenDao.class);

        val jankenCountBeforeTest = tm.transactional(jankenDao::count);

        // 実行

        try {
            service.play(1L, Hand.STONE, 2L, Hand.STONE);

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
    public Optional<JankenDetail> findById(Transaction tx, long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count(Transaction tx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<JankenDetail> insertAll(Transaction tx, List<JankenDetail> jankenDetails) {
        throw new UnsupportedOperationException();
    }

}
