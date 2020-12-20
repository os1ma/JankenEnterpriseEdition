package com.example.janken.domain.model.player;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository {

    List<Player> findAllOrderById();

    Optional<Player> findPlayerById(String playerId);

}
