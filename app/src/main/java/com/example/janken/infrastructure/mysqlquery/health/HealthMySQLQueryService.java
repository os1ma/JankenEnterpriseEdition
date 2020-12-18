package com.example.janken.infrastructure.mysqlquery.health;

import com.example.janken.application.query.health.HealthQueryService;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class HealthMySQLQueryService implements HealthQueryService {

    private DSLContext db;

    @Override
    public boolean isHealthy() {
        db.selectOne().fetch();
        // 例外が発生しなければ正常
        return true;
    }

}
