package com.example.janken.application.service.janken;

import com.example.janken.domain.model.janken.Hand;
import com.example.janken.domain.model.janken.JankenExecutor;
import com.example.janken.domain.model.janken.JankenRepository;
import com.example.janken.domain.model.player.Player;
import com.example.janken.domain.model.player.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class JankenApplicationService {

    private JankenRepository jankenRepository;
    private PlayerRepository playerRepository;
    private JankenExecutor jankenExecutor;

    /**
     * じゃんけんを実行し、結果を保存して、勝者を返します。
     */
    public Optional<Player> play(String player1Id, Hand player1Hand,
                                 String player2Id, Hand player2Hand) {

        val janken = jankenExecutor.play(player1Id, player1Hand, player2Id, player2Hand);

        jankenRepository.save(janken);

        return janken.winnerPlayerId()
                .map(playerId -> playerRepository.findPlayerById(playerId).orElseThrow());
    }

}
