package com.example.janken.application.service;

import com.example.janken.domain.model.player.Player;
import com.example.janken.domain.model.player.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class PlayerApplicationService {

    private PlayerRepository playerRepository;

    public Player findPlayerById(String playerId) {
        return playerRepository.findPlayerById(playerId);
    }

}
