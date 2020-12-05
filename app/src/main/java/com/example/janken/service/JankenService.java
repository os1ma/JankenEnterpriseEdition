package com.example.janken.service;

import com.example.janken.dao.JankenDao;
import com.example.janken.dao.JankenDetailDao;
import com.example.janken.model.*;
import lombok.val;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class JankenService {

    private JankenDao jankenDao = new JankenDao();
    private JankenDetailDao jankenDetailDao = new JankenDetailDao();

    /**
     * じゃんけんを実行し、勝者を返します。
     */
    public Optional<Player> play(Player player1, Hand player1Hand,
                                 Player player2, Hand player2Hand) {

        // 勝敗判定

        Result player1Result;
        Result player2Result;
        if (player1Hand.equals(Hand.STONE)) {
            // プレイヤーがグーの場合

            if (player2Hand.equals(Hand.STONE)) {
                player1Result = Result.DRAW;
                player2Result = Result.DRAW;
            } else if (player2Hand.equals(Hand.PAPER)) {
                player1Result = Result.LOSE;
                player2Result = Result.WIN;
            } else {
                player1Result = Result.WIN;
                player2Result = Result.LOSE;
            }

        } else if (player1Hand.equals(Hand.PAPER)) {
            // プレイヤーがパーの場合

            if (player2Hand.equals(Hand.STONE)) {
                player1Result = Result.WIN;
                player2Result = Result.LOSE;
            } else if (player2Hand.equals(Hand.PAPER)) {
                player1Result = Result.DRAW;
                player2Result = Result.DRAW;
            } else {
                player1Result = Result.LOSE;
                player2Result = Result.WIN;
            }

        } else {
            // プレイヤーがチョキの場合

            if (player2Hand.equals(Hand.STONE)) {
                player1Result = Result.LOSE;
                player2Result = Result.WIN;
            } else if (player2Hand.equals(Hand.PAPER)) {
                player1Result = Result.WIN;
                player2Result = Result.LOSE;
            } else {
                player1Result = Result.DRAW;
                player2Result = Result.DRAW;
            }
        }

        // じゃんけんを生成

        val playedAt = LocalDateTime.now();
        val janken = new Janken(null, playedAt);

        // じゃんけんを保存

        val jankenWithId = jankenDao.insert(janken);

        // じゃんけん明細を生成

        val jankenDetail1 = new JankenDetail(null, jankenWithId.getId(), player1.getId(), player1Hand, player1Result);
        val jankenDetail2 = new JankenDetail(null, jankenWithId.getId(), player2.getId(), player2Hand, player2Result);
        val jankenDetails = List.of(jankenDetail1, jankenDetail2);

        // じゃんけん明細を保存

        jankenDetailDao.insertAll(jankenDetails);

        // 勝者を返却

        if (player1Result.equals(Result.WIN)) {
            return Optional.of(player1);
        } else if (player2Result.equals(Result.WIN)) {
            return Optional.of(player2);
        } else {
            return Optional.empty();
        }
    }

}