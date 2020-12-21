package com.example.janken.infrastructure.jdbctransaction;

import com.example.janken.domain.model.janken.Janken;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.domain.transaction.TransactionManager;
import com.example.janken.infrastructure.dao.JankenDao;
import com.example.janken.infrastructure.mysqldao.JankenMySQLDao;
import com.example.janken.registry.ServiceLocator;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * {@link Transaction} 型のキャストに関するサンプルコード
 */
public class TransactionCastingSample {

    @Test
    public <T extends Transaction> void ServiceLocatorに適切に登録すれば例外が発生しない() {
        ServiceLocator.register(TransactionManager.class, JDBCTransactionManager.class);
        ServiceLocator.register(JankenDao.class, JankenMySQLDao.class);

        TransactionManager<T> tm = ServiceLocator.resolve(TransactionManager.class);
        JankenDao<T> jankenDao = ServiceLocator.resolve(JankenDao.class);

        try {
            tm.transactional(tx -> {
                jankenDao.count(tx);
            });
        } catch (Throwable e) {
            // 例外が発生した場合はテスト失敗
            fail(e);
        }
    }

    @Test
    public <T extends Transaction> void ServiceLocatorへの登録が不適切だと実行時に例外が発生する() {
        ServiceLocator.register(TransactionManager.class, JDBCTransactionManager.class);
        // JankenMySQLDao を登録すべきなのに JankenNonMySQLDao を登録
        ServiceLocator.register(JankenDao.class, JankenNonMySQLDao.class);

        TransactionManager<T> tm = ServiceLocator.resolve(TransactionManager.class);
        JankenDao<T> jankenDao = ServiceLocator.resolve(JankenDao.class);

        tm.transactional(tx -> {
            assertThrows(ClassCastException.class, () -> jankenDao.count(tx));
        });
    }

    @Test
    public <T extends Transaction> void Transaction型を扱うクラスをとりまとめるクラスを使えば型安全に取り出せる() {
        ServiceLocator.register(TransactionalClassRegistry.class, MySQLTransactionalClassRegistry.class);

        TransactionalClassRegistry<T> registry = ServiceLocator.resolve(TransactionalClassRegistry.class);

        val tm = registry.tm();
        val jankenDao = registry.jankenDao();

        try {
            tm.transactional(tx -> {
                jankenDao.count(tx);
            });
        } catch (Throwable e) {
            // 例外が発生した場合はテスト失敗
            fail(e);
        }
    }

}

class NonMySQLTransaction implements Transaction {

    @Override
    public void commit() {
        // Do nothing
    }

    @Override
    public void close() {
        // Do nothing
    }
}

@NoArgsConstructor
class JankenNonMySQLDao implements JankenDao<NonMySQLTransaction> {

    @Override
    public List<Janken> findAllOrderByPlayedAt(NonMySQLTransaction tx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Janken> findById(NonMySQLTransaction tx, String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count(NonMySQLTransaction tx) {
        return 0;
    }

    @Override
    public void insert(NonMySQLTransaction tx, Janken janken) {
        throw new UnsupportedOperationException();
    }

}

interface TransactionalClassRegistry<T extends Transaction> {

    TransactionManager<T> tm();

    JankenDao<T> jankenDao();

}

@NoArgsConstructor
class MySQLTransactionalClassRegistry implements TransactionalClassRegistry<JDBCTransaction> {

    @Override
    public TransactionManager<JDBCTransaction> tm() {
        return new JDBCTransactionManager();
    }

    @Override
    public JankenDao<JDBCTransaction> jankenDao() {
        return new JankenMySQLDao();
    }

}
