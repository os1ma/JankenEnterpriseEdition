package com.example.janken.application.service.player;

import com.example.janken.domain.model.janken.JankenRepository;
import com.example.janken.domain.model.player.Player;
import com.example.janken.domain.model.player.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class PlayerApplicationService {

    private PlayerRepository playerRepository;
    private JankenRepository jankenRepository;

    public PlayerFindAllOutput findAll() {
        val players = playerRepository.findAllOrderById();

        val jankens = jankenRepository.findAll();

        List<PlayerFindAllOutputPlayer> outputPlayers = players.stream()
                .map(p -> {
                    val winCount = jankens.stream()
                            .filter(j -> j.isWinner(p))
                            .count();

                    return new PlayerFindAllOutputPlayer(
                            p.getId(),
                            p.getName(),
                            winCount);
                })
                .collect(Collectors.toList());

        return new PlayerFindAllOutput(outputPlayers);
    }

    public Optional<Player> findPlayerById(String playerId) {
        return playerRepository.findPlayerById(playerId);
    }

}
