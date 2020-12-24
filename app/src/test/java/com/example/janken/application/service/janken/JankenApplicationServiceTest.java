package com.example.janken.application.service.janken;

import com.example.janken.application.exception.ApplicationException;
import com.example.janken.domain.model.janken.Hand;
import com.example.janken.domain.model.janken.JankenExecutor;
import com.example.janken.infrastructure.jdbctransaction.JDBCTransactionManager;
import com.example.janken.infrastructure.mysqlrepository.JankenMySQLRepository;
import com.example.janken.infrastructure.mysqlrepository.PlayerMySQLRepository;
import lombok.val;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JankenApplicationServiceTest {

    private static final String NON_EXISTING_PLAYER_ID = "not exist";
    private static final String EXISTING_PLAYER_ID = "1";

    @Test
    public void 存在しないプレイヤーIDの場合例外がスローされる() {
        val tm = new JDBCTransactionManager();

        tm.transactional(tx -> {
            val conn = tx.getConn();
            val dslContext = DSL.using(conn, SQLDialect.MYSQL);

            val jankenRepository = new JankenMySQLRepository(dslContext);
            val playerRepository = new PlayerMySQLRepository(dslContext);
            val jankenExecutor = new JankenExecutor();
            val service = new JankenApplicationService(jankenRepository, playerRepository, jankenExecutor);

            assertThrows(ApplicationException.class,
                    () -> service.play(NON_EXISTING_PLAYER_ID, Hand.STONE, EXISTING_PLAYER_ID, Hand.STONE));
        });

    }

}
