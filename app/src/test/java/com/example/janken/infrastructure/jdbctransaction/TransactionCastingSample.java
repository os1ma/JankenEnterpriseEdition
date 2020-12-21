package com.example.janken.infrastructure.jdbctransaction;

import com.example.janken.domain.model.janken.Janken;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.domain.transaction.TransactionManager;
import com.example.janken.infrastructure.dao.JankenDao;
import com.example.janken.infrastructure.mysqldao.JankenMySQLDao;
import com.example.janken.registry.ServiceLocator;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * {@link Transaction} 型のキャストに関するサンプルコード
 */
public class TransactionCastingSample {

    @Nested
    class ServiceLocatorを使って無理やり取り出す場合場合 {

        @BeforeEach
        public void setup() {
            ServiceLocator.reset();
        }

        @Test
        public void ServiceLocatorに適切に登録すれば例外が発生しない() {
            ServiceLocator.register(TransactionManager.class, JDBCTransactionManager.class);
            ServiceLocator.register(JankenDao.class, JankenMySQLDao.class);

            val service = new SampleServiceResolvingTransactionManagerAndJankenDao<>();

            try {
                service.run();
            } catch (Throwable e) {
                // 例外が発生した場合はテスト失敗
                fail(e);
            }
        }

        @Test
        public void ServiceLocatorへの登録が不適切だと実行時に例外が発生する() {
            ServiceLocator.register(TransactionManager.class, JDBCTransactionManager.class);
            // JankenMySQLDao を登録すべきなのに JankenNonMySQLDao を登録
            ServiceLocator.register(JankenDao.class, JankenNonMySQLDao.class);

            val service = new SampleServiceResolvingTransactionManagerAndJankenDao<>();

            assertThrows(ClassCastException.class, service::run);
        }

        private class SampleServiceResolvingTransactionManagerAndJankenDao<T extends Transaction> {
            private TransactionManager<T> tm = ServiceLocator.resolve(TransactionManager.class);
            private JankenDao<T> jankenDao = ServiceLocator.resolve(JankenDao.class);

            public void run() {
                tm.transactional(tx -> {
                    jankenDao.count(tx);
                });
            }

        }

    }

    @Nested
    class Transaction型を扱うクラスをとりまとめるクラスをServiceLocatorに登録する場合 {

        @Test
        public void Transaction型を扱うクラスをとりまとめるクラスをServiceLocatorに登録すれば型安全に取り出せる() {
            ServiceLocator.register(TransactionalClassFactory.class, MySQLTransactionalClassFactory.class);

            val service = new SampleServiceResolvingTransactionalClassFactory<>();

            try {
                service.run();
            } catch (Throwable e) {
                // 例外が発生した場合はテスト失敗
                fail(e);
            }
        }

        private class SampleServiceResolvingTransactionalClassFactory<T extends Transaction> {
            private TransactionalClassFactory<T> factory = ServiceLocator.resolve(TransactionalClassFactory.class);
            private TransactionManager<T> tm = factory.tm();
            private JankenDao<T> jankenDao = factory.jankenDao();

            public void run() {
                tm.transactional(tx -> {
                    jankenDao.count(tx);
                });
            }

        }

    }

    @Nested
    class DIを使う場合 {

        @Test
        public void DIなら型安全に扱える() {
            val tm = new JDBCTransactionManager();
            val jankenDao = new JankenMySQLDao();

            val service = new SampleService<>(tm, jankenDao);

            try {
                service.run();
            } catch (Throwable e) {
                // 例外が発生した場合はテスト失敗
                fail(e);
            }
        }

        @AllArgsConstructor
        private class SampleService<T extends Transaction> {
            private TransactionManager<T> tm;
            private JankenDao<T> jankenDao;

            public void run() {
                tm.transactional(tx -> {
                    jankenDao.count(tx);
                });
            }

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

interface TransactionalClassFactory<T extends Transaction> {

    TransactionManager<T> tm();

    JankenDao<T> jankenDao();

}

@NoArgsConstructor
class MySQLTransactionalClassFactory implements TransactionalClassFactory<JDBCTransaction> {

    @Override
    public TransactionManager<JDBCTransaction> tm() {
        return new JDBCTransactionManager();
    }

    @Override
    public JankenDao<JDBCTransaction> jankenDao() {
        return new JankenMySQLDao();
    }

}
