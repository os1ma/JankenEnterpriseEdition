package com.example.janken.application.service;

import com.example.janken.domain.model.janken.Hand;
import com.example.janken.domain.model.janken.Janken;
import com.example.janken.domain.model.janken.JankenRepository;
import com.example.janken.domain.model.player.Player;
import com.example.janken.domain.model.player.PlayerRepository;
import com.example.janken.domain.transaction.TransactionManager;
import com.example.janken.registry.ServiceLocator;
import lombok.val;

import java.util.Optional;

public class JankenApplicationService {

    private TransactionManager tm = ServiceLocator.resolve(TransactionManager.class);

    private JankenRepository jankenRepository = ServiceLocator.resolve(JankenRepository.class);
    private PlayerRepository playerRepository = ServiceLocator.resolve(PlayerRepository.class);

    /**
     * じゃんけんを実行し、結果を保存して、勝者を返します。
     */
    public Optional<Player> play(long player1Id, Hand player1Hand,
                                 long player2Id, Hand player2Hand) {

        return tm.transactional(tx -> {

            val janken = Janken.play(player1Id, player1Hand, player2Id, player2Hand);

            jankenRepository.save(tx, janken);

            return janken.winnerPlayerId()
                    .map(playerId -> playerRepository.findPlayerById(tx, playerId));
        });

    }

}
