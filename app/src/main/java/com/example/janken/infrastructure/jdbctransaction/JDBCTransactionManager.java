package com.example.janken.infrastructure.jdbctransaction;

import com.example.janken.domain.transaction.TransactionManager;
import lombok.val;

import java.util.function.Consumer;
import java.util.function.Function;

public class JDBCTransactionManager implements TransactionManager<JDBCTransaction> {

    @Override
    public <T> T transactional(Function<JDBCTransaction, T> f) {
        try (val t = new JDBCTransaction()) {
            T result = f.apply(t);
            t.commit();
            return result;
        }
    }

    @Override
    public void transactional(Consumer<JDBCTransaction> c) {
        try (val t = new JDBCTransaction()) {
            c.accept(t);
            t.commit();
        }
    }

}
