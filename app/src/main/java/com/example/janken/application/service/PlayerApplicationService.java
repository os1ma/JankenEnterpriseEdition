package com.example.janken.application.service;

import com.example.janken.domain.model.player.Player;
import com.example.janken.domain.model.player.PlayerRepository;
import com.example.janken.domain.transaction.TransactionManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PlayerApplicationService {

    private TransactionManager tm;

    private PlayerRepository playerRepository;

    public Player findPlayerById(String playerId) {
        return tm.transactional(tx -> {
            return playerRepository.findPlayerById(tx, playerId);
        });
    }

}
