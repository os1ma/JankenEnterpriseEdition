package com.example.janken.businesslogic.dao;

import com.example.janken.businesslogic.model.Player;

public interface PlayerDao {

    Player findPlayerById(long playerId);

}
