package com.example.janken.infrastructure.jdbctransaction;

import com.example.janken.domain.model.janken.*;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.infrastructure.dao.JankenDetailDao;
import com.example.janken.infrastructure.mysqldao.JankenDetailMySQLDao;
import com.example.janken.infrastructure.mysqldao.JankenMySQLDao;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JDBCTransactionManagerTest {

    private JDBCTransactionManager tm = new JDBCTransactionManager();
    private JankenExecutor jankenExecutor = new JankenExecutor();

    @Test
    public void トランザクションの中で保存されたデータが全て反映されている() {
        // 準備
        val jankenDao = new JankenMySQLDao();
        val jankenDetailDao = new JankenDetailMySQLDao();

        val jankenCountBeforeTest = tm.transactional(jankenDao::count);

        // 実行
        tm.transactional(tx -> {
            val handSelection1 = new HandSelection("1", Hand.STONE);
            val handSelection2 = new HandSelection("2", Hand.STONE);
            val handSelections = HandSelections.of(handSelection1, handSelection2);
            val janken = jankenExecutor.play(handSelections);

            jankenDao.insert(tx, janken);
            jankenDetailDao.insertAll(tx, janken.getDetails());
        });

        // 検証
        val jankenCountAfterTest = tm.transactional(jankenDao::count);
        assertEquals(jankenCountBeforeTest + 1, jankenCountAfterTest, "じゃんけんの件数が 1 つ増えている");
    }

    @Test
    public void トランザクション処理の途中で例外が発生したら全て保存されない() {
        // 準備
        val jankenDao = new JankenMySQLDao();
        val jankenDetailDao = new JankenDetailErrorDao();

        val jankenCountBeforeTest = tm.transactional(jankenDao::count);

        // 実行
        try {
            tm.transactional(tx -> {
                val handSelection1 = new HandSelection("1", Hand.STONE);
                val handSelection2 = new HandSelection("2", Hand.STONE);
                val handSelections = HandSelections.of(handSelection1, handSelection2);
                val janken = jankenExecutor.play(handSelections);

                jankenDao.insert(tx, janken);
                jankenDetailDao.insertAll(tx, janken.getDetails());
            });

            // 例外が発生しなかった場合
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
class JankenDetailErrorDao implements JankenDetailDao<Transaction> {

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
