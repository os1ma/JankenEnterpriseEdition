package com.example.janken;

import com.example.janken.application.service.JankenApplicationService;
import com.example.janken.application.service.PlayerApplicationService;
import com.example.janken.infrastructure.dao.JankenDao;
import com.example.janken.infrastructure.dao.JankenDetailDao;
import com.example.janken.infrastructure.dao.PlayerDao;
import com.example.janken.domain.model.janken.JankenRepository;
import com.example.janken.domain.model.player.PlayerRepository;
import com.example.janken.domain.transaction.TransactionManager;
import com.example.janken.infrastructure.jdbctransaction.JDBCTransactionManager;
import com.example.janken.infrastructure.mysqldao.JankenDetailMySQLDao;
import com.example.janken.infrastructure.mysqldao.JankenMySQLDao;
import com.example.janken.infrastructure.mysqldao.PlayerMySQLDao;
import com.example.janken.infrastructure.mysqlrepository.JankenMySQLRepository;
import com.example.janken.infrastructure.mysqlrepository.PlayerMySQLRepository;
import com.example.janken.presentation.cli.controller.JankenCLIController;
import com.example.janken.registry.ServiceLocator;

public class JankenCLIApplication {

    public static void main(String[] args) {

        // 依存解決の設定

        ServiceLocator.register(TransactionManager.class, JDBCTransactionManager.class);

        ServiceLocator.register(JankenCLIController.class, JankenCLIController.class);

        ServiceLocator.register(PlayerApplicationService.class, PlayerApplicationService.class);
        ServiceLocator.register(JankenApplicationService.class, JankenApplicationService.class);

        ServiceLocator.register(PlayerRepository.class, PlayerMySQLRepository.class);
        ServiceLocator.register(JankenRepository.class, JankenMySQLRepository.class);

        ServiceLocator.register(PlayerDao.class, PlayerMySQLDao.class);
        ServiceLocator.register(JankenDao.class, JankenMySQLDao.class);
        ServiceLocator.register(JankenDetailDao.class, JankenDetailMySQLDao.class);

        // 実行

        ServiceLocator.resolve(JankenCLIController.class).play();

    }

}