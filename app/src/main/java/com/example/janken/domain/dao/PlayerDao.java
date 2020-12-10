package com.example.janken.domain.dao;

import com.example.janken.domain.model.Player;
import com.example.janken.framework.Transaction;

public interface PlayerDao {

    Player findPlayerById(Transaction tx, long playerId);

}
