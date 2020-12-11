package com.example.janken.application.service;

import com.example.janken.domain.dao.JankenDao;
import com.example.janken.domain.dao.JankenDetailDao;
import com.example.janken.domain.dao.PlayerDao;
import com.example.janken.domain.model.Hand;
import com.example.janken.domain.model.Janken;
import com.example.janken.domain.model.Player;
import com.example.janken.domain.transaction.TransactionManager;
import com.example.janken.registry.ServiceLocator;
import lombok.val;

import java.util.Optional;

public class JankenApplicationService {

    private TransactionManager tm = ServiceLocator.resolve(TransactionManager.class);

    private JankenDao jankenDao = ServiceLocator.resolve(JankenDao.class);
    private JankenDetailDao jankenDetailDao = ServiceLocator.resolve(JankenDetailDao.class);
    private PlayerDao playerDao = ServiceLocator.resolve(PlayerDao.class);

    /**
     * じゃんけんを実行し、結果を保存して、勝者を返します。
     */
    public Optional<Player> play(long player1Id, Hand player1Hand,
                                 long player2Id, Hand player2Hand) {

        return tm.transactional(tx -> {

            val janken = Janken.play(player1Id, player1Hand, player2Id, player2Hand);

            val jankenWithId = jankenDao.insert(tx, janken);
            jankenDetailDao.insertAll(tx, jankenWithId.details());

            return jankenWithId.winnerPlayerId()
                    .map(playerId -> playerDao.findPlayerById(tx, playerId));
        });

    }

}
