package com.example.janken.infrastructure.jdbctransaction;

import com.example.janken.domain.transaction.Transaction;
import com.example.janken.domain.transaction.TransactionManager;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class JDBCTransactionManager implements TransactionManager {

    @Override
    public <T> T transactional(Function<Transaction, T> f) {
        try (val t = new JDBCTransaction()) {
            T result = f.apply(t);
            t.commit();
            return result;
        }
    }

    @Override
    public void transactional(Consumer<Transaction> c) {
        try (val t = new JDBCTransaction()) {
            c.accept(t);
            t.commit();
        }
    }

}
