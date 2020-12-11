package com.example.janken.application.service;

import com.example.janken.domain.dao.JankenDao;
import com.example.janken.domain.dao.JankenDetailDao;
import com.example.janken.domain.model.*;
import com.example.janken.domain.transaction.TransactionManager;
import com.example.janken.registry.ServiceLocator;
import lombok.val;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class JankenService {

    private TransactionManager tm = ServiceLocator.resolve(TransactionManager.class);

    private JankenDao jankenDao = ServiceLocator.resolve(JankenDao.class);
    private JankenDetailDao jankenDetailDao = ServiceLocator.resolve(JankenDetailDao.class);

    /**
     * じゃんけんを実行し、勝者を返します。
     */
    public Optional<Player> play(Player player1, Hand player1Hand,
                                 Player player2, Hand player2Hand) {

        return tm.transactional(tx -> {

            // 勝敗判定

            Result player1Result;
            Result player2Result;

            if (player1Hand.wins(player2Hand)) {
                player1Result = Result.WIN;
                player2Result = Result.LOSE;

            } else if (player2Hand.wins(player1Hand)) {
                player1Result = Result.LOSE;
                player2Result = Result.WIN;

            } else {
                player1Result = Result.DRAW;
                player2Result = Result.DRAW;
            }

            // じゃんけんを生成

            val playedAt = LocalDateTime.now();
            val janken = new Janken(null, playedAt);

            // じゃんけんを保存

            val jankenWithId = jankenDao.insert(tx, janken);

            // じゃんけん明細を生成

            val jankenDetail1 = new JankenDetail(null, jankenWithId.getId(), player1.getId(), player1Hand, player1Result);
            val jankenDetail2 = new JankenDetail(null, jankenWithId.getId(), player2.getId(), player2Hand, player2Result);
            val jankenDetails = List.of(jankenDetail1, jankenDetail2);

            // じゃんけん明細を保存

            jankenDetailDao.insertAll(tx, jankenDetails);

            // 勝者を返却

            if (player1Result.equals(Result.WIN)) {
                return Optional.of(player1);
            } else if (player2Result.equals(Result.WIN)) {
                return Optional.of(player2);
            } else {
                return Optional.empty();
            }
        });

    }

}
