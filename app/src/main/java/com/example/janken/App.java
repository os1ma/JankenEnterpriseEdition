package com.example.janken;

import com.example.janken.businesslogic.dao.JankenDao;
import com.example.janken.businesslogic.dao.JankenDetailDao;
import com.example.janken.businesslogic.dao.PlayerDao;
import com.example.janken.businesslogic.service.JankenService;
import com.example.janken.businesslogic.service.PlayerService;
import com.example.janken.dataaccess.csvdao.JankenCsvDao;
import com.example.janken.dataaccess.csvdao.JankenDetailCsvDao;
import com.example.janken.dataaccess.csvdao.PlayerCsvDao;
import com.example.janken.framework.ServiceLocator;
import com.example.janken.presentation.controller.JankenController;

public class App {

    public static void main(String[] args) {

        // 依存解決の設定

        ServiceLocator.register(JankenController.class, JankenController.class);

        ServiceLocator.register(PlayerService.class, PlayerService.class);
        ServiceLocator.register(JankenService.class, JankenService.class);

        ServiceLocator.register(PlayerDao.class, PlayerCsvDao.class);
        ServiceLocator.register(JankenDao.class, JankenCsvDao.class);
        ServiceLocator.register(JankenDetailDao.class, JankenDetailCsvDao.class);

        // 実行

        ServiceLocator.resolve(JankenController.class).play();

    }

}