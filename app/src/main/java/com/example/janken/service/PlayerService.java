package com.example.janken.service;

import com.example.janken.dao.PlayerDao;
import com.example.janken.model.Player;

public class PlayerService {

    private PlayerDao playerDao = new PlayerDao();

    public Player findPlayerById(long playerId) {
        return playerDao.findPlayerById(playerId);
    }

}
