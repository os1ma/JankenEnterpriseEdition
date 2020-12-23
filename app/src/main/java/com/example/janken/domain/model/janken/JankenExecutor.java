package com.example.janken.domain.model.janken;

import lombok.val;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class JankenExecutor {

    public Janken play(String player1Id, Hand player1Hand,
                       String player2Id, Hand player2Hand) {
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

        // ID を生成

        val jankenId = generateId();
        val jankenDetail1Id = generateId();
        val jankenDetail2Id = generateId();

        // じゃんけん明細を生成

        val detail1 = new JankenDetail(jankenDetail1Id, jankenId, player1Id, player1Hand, player1Result);
        val detail2 = new JankenDetail(jankenDetail2Id, jankenId, player2Id, player2Hand, player2Result);

        // じゃんけんを生成

        val playedAt = LocalDateTime.now();
        return new Janken(jankenId, playedAt, detail1, detail2);
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

}
