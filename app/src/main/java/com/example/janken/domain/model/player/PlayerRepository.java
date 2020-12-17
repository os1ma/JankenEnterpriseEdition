package com.example.janken.domain.model.player;

public interface PlayerRepository {

    Player findPlayerById(String playerId);

}
