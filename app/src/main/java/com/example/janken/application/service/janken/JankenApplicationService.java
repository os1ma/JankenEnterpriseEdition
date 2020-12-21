package com.example.janken.application.service.janken;

import com.example.janken.domain.model.janken.Hand;
import com.example.janken.domain.model.janken.Janken;
import com.example.janken.domain.model.janken.JankenRepository;
import com.example.janken.domain.model.player.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class JankenApplicationService {

    private JankenRepository jankenRepository;
    private PlayerRepository playerRepository;

    public JankenPlayOutput play(String player1Id, Hand player1Hand,
                                 String player2Id, Hand player2Hand) {
        val janken = Janken.play(player1Id, player1Hand, player2Id, player2Hand);

        jankenRepository.save(janken);

        val player1 = playerRepository.findPlayerById(player1Id).orElseThrow();
        val player2 = playerRepository.findPlayerById(player2Id).orElseThrow();
        val maybeWinner = janken.winnerPlayerId()
                .map(playerId -> playerRepository.findPlayerById(playerId).orElseThrow());
        return new JankenPlayOutput(player1, player2, maybeWinner);
    }

}
