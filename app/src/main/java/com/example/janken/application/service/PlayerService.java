package com.example.janken.application.service;

import com.example.janken.domain.dao.PlayerDao;
import com.example.janken.domain.model.Player;
import com.example.janken.framework.ServiceLocator;

public class PlayerService {

    private PlayerDao playerDao = ServiceLocator.resolve(PlayerDao.class);

    public Player findPlayerById(long playerId) {
        return playerDao.findPlayerById(playerId);
    }

}
