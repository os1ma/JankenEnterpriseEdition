package com.example.janken.application.service;

import com.example.janken.domain.model.player.Player;
import com.example.janken.domain.model.player.PlayerRepository;
import com.example.janken.domain.transaction.TransactionManager;
import com.example.janken.registry.ServiceLocator;

public class PlayerApplicationService {

    private TransactionManager tm = ServiceLocator.resolve(TransactionManager.class);

    private PlayerRepository playerRepository = ServiceLocator.resolve(PlayerRepository.class);

    public Player findPlayerById(String playerId) {
        return tm.transactional(tx -> {
            return playerRepository.findPlayerById(tx, playerId);
        });
    }

}
