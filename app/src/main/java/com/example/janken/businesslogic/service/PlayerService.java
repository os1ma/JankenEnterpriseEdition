package com.example.janken.businesslogic.service;

import com.example.janken.businesslogic.dao.PlayerDao;
import com.example.janken.businesslogic.model.Player;
import com.example.janken.framework.ServiceLocator;

public class PlayerService {

    private PlayerDao playerDao = ServiceLocator.resolve(PlayerDao.class);

    public Player findPlayerById(long playerId) {
        return playerDao.findPlayerById(playerId);
    }

}
