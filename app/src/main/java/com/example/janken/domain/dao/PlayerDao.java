package com.example.janken.domain.dao;

import com.example.janken.domain.model.Player;
import com.example.janken.domain.transaction.Transaction;

public interface PlayerDao {

    Player findPlayerById(Transaction tx, long playerId);

}
