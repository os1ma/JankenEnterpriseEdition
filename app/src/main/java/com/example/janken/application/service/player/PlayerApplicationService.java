package com.example.janken.application.service.player;

import com.example.janken.application.query.PlayerListQueryModel;
import com.example.janken.application.query.PlayerQueryService;
import com.example.janken.domain.model.player.Player;
import com.example.janken.domain.model.player.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class PlayerApplicationService {

    private PlayerRepository playerRepository;
    private PlayerQueryService playerQueryService;

    public PlayerListQueryModel findAll() {
        return playerQueryService.queryAll();
    }

    public Optional<Player> findPlayerById(String playerId) {
        return playerRepository.findPlayerById(playerId);
    }

}
