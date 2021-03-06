package com.example.janken;

import com.example.janken.application.scenario.PlayJankenScenario;
import com.example.janken.application.service.janken.JankenApplicationService;
import com.example.janken.application.service.player.PlayerApplicationService;
import com.example.janken.domain.model.janken.JankenExecutor;
import com.example.janken.infrastructure.jdbctransaction.JDBCTransactionManager;
import com.example.janken.infrastructure.mysqlquery.player.PlayerMySQLQueryService;
import com.example.janken.infrastructure.mysqlrepository.JankenMySQLRepository;
import com.example.janken.infrastructure.mysqlrepository.PlayerMySQLRepository;
import com.example.janken.presentation.cli.janken.PlayJankenStandardInputController;
import com.example.janken.presentation.cli.janken.PlayJankenStandardOutputPresenter;
import lombok.val;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class JankenCLIApplication {

    public static void main(String[] args) {

        val tm = new JDBCTransactionManager();

        tm.transactional(tx -> {
            val conn = tx.getConn();
            val dslContext = DSL.using(conn, SQLDialect.MYSQL);

            // 依存解決の設定

            val playerRepository = new PlayerMySQLRepository(dslContext);
            val jankenRepository = new JankenMySQLRepository(dslContext);

            val playerQueryService = new PlayerMySQLQueryService(dslContext);

            val jankenExecutor = new JankenExecutor();

            val playerApplicationService = new PlayerApplicationService(playerRepository, playerQueryService);
            val jankenApplicationService = new JankenApplicationService(jankenRepository, playerRepository, jankenExecutor);

            val playJankenController = new PlayJankenStandardInputController();
            val playJankenPresenter = new PlayJankenStandardOutputPresenter();

            val playJankenScenario = new PlayJankenScenario(
                    playJankenController, playJankenPresenter,
                    playerApplicationService, jankenApplicationService);

            // 実行

            playJankenScenario.run();

        });

    }

}
