package com.example.janken.businesslogic.service;

import com.example.janken.dataaccess.csvdao.PlayerCsvDao;
import com.example.janken.dataaccess.model.Player;

public class PlayerService {

    private PlayerCsvDao playerCsvDao = new PlayerCsvDao();

    public Player findPlayerById(long playerId) {
        return playerCsvDao.findPlayerById(playerId);
    }

}
