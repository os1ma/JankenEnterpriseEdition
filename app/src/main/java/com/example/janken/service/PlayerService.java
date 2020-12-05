package com.example.janken.service;

import com.example.janken.csvdao.PlayerCsvDao;
import com.example.janken.model.Player;

public class PlayerService {

    private PlayerCsvDao playerCsvDao = new PlayerCsvDao();

    public Player findPlayerById(long playerId) {
        return playerCsvDao.findPlayerById(playerId);
    }

}
