package com.example.janken.domain.dao;

import com.example.janken.domain.model.Player;

public interface PlayerDao {

    Player findPlayerById(long playerId);

}
