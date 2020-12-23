package com.example.janken.application.service.janken;

import com.example.janken.domain.model.janken.Hand;
import com.example.janken.domain.model.janken.HandSelection;
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

        val handSelection1 = new HandSelection(player1Id, player1Hand);
        val handSelection2 = new HandSelection(player2Id, player2Hand);

        val janken = jankenExecutor.play(handSelection1, handSelection2);

        jankenRepository.save(janken);

        return janken.winnerPlayerIds()
                .stream()
                .findFirst()
                .map(playerId -> playerRepository.findPlayerById(playerId).orElseThrow());
    }

}
