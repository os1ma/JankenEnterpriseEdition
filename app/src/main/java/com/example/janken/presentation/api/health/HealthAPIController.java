package com.example.janken.presentation.api.health;

import com.example.janken.domain.transaction.TransactionManager;
import com.example.janken.infrastructure.jdbctransaction.JDBCTransactionManager;
import com.example.janken.infrastructure.jdbctransaction.SimpleJDBCWrapper;
import com.example.janken.infrastructure.jdbctransaction.SingleRowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthAPIController {

    private TransactionManager tm = new JDBCTransactionManager();
    private SimpleJDBCWrapper simpleJDBCWrapper = new SimpleJDBCWrapper();

    @GetMapping
    public HealthResponseBody get() {
        tm.transactional(tx -> {
            simpleJDBCWrapper.findFirst(tx, new SingleRowMapper<Long>(), "SELECT 1");
        });

        return new HealthResponseBody(200);
    }

}
