package com.example.janken.application.service;

import com.example.janken.domain.dao.PlayerDao;
import com.example.janken.domain.model.Player;
import com.example.janken.registry.ServiceLocator;
import com.example.janken.domain.transaction.TransactionManager;
import lombok.val;

public class PlayerService {

    private TransactionManager tm = ServiceLocator.resolve(TransactionManager.class);

    private PlayerDao playerDao = ServiceLocator.resolve(PlayerDao.class);

    public Player findPlayerById(long playerId) {
        val tx = tm.startTransaction();

        return playerDao.findPlayerById(tx, playerId);
    }

}
