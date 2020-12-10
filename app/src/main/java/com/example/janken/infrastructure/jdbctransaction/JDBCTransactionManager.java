package com.example.janken.infrastructure.jdbctransaction;

import com.example.janken.framework.Transaction;
import com.example.janken.framework.TransactionManager;

public class JDBCTransactionManager implements TransactionManager {

    @Override
    public Transaction startTransaction() {
        return new JDBCTransaction();
    }

}
