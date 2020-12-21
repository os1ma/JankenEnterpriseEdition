package com.example.janken.infrastructure.dao;

import com.example.janken.domain.model.player.Player;
import com.example.janken.domain.transaction.Transaction;

public interface PlayerDao<T extends Transaction> {

    Player findPlayerById(T tx, String playerId);

}
