package com.example.janken.infrastructure.jdbctransaction;

import com.example.janken.domain.transaction.Transaction;
import com.example.janken.domain.transaction.TransactionManager;

public class JDBCTransactionManager implements TransactionManager {

    @Override
    public Transaction startTransaction() {
        return new JDBCTransaction();
    }

}
