package com.example.janken.domain.model.player;

import com.example.janken.domain.transaction.Transaction;

public interface PlayerRepository {

    Player findPlayerById(Transaction tx, long playerId);

}
