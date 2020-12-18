package com.example.janken;

import com.example.janken.application.service.janken.JankenApplicationService;
import com.example.janken.application.service.player.PlayerApplicationService;
import com.example.janken.infrastructure.jdbctransaction.JDBCTransaction;
import com.example.janken.infrastructure.jdbctransaction.JDBCTransactionManager;
import com.example.janken.infrastructure.mysqlquery.PlayerMySQLQueryService;
import com.example.janken.infrastructure.mysqlrepository.JankenMySQLRepository;
import com.example.janken.infrastructure.mysqlrepository.PlayerMySQLRepository;
import com.example.janken.presentation.cli.controller.JankenCLIController;
import lombok.val;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class JankenCLIApplication {

    public static void main(String[] args) {

        val tm = new JDBCTransactionManager();

        tm.transactional(tx -> {
            val conn = ((JDBCTransaction) tx).getConn();
            val dslContext = DSL.using(conn, SQLDialect.MYSQL);

            // 依存解決の設定

            val playerRepository = new PlayerMySQLRepository(dslContext);
            val jankenRepository = new JankenMySQLRepository(dslContext);

            val playerQueryService = new PlayerMySQLQueryService(dslContext);

            val playerApplicationService = new PlayerApplicationService(playerRepository, playerQueryService);
            val jankenApplicationService = new JankenApplicationService(jankenRepository, playerRepository);

            val jankenCliController = new JankenCLIController(playerApplicationService, jankenApplicationService);

            // 実行

            jankenCliController.play();

        });

    }

}