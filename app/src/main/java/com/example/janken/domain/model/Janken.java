package com.example.janken.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Janken {

    public static Janken play(Long player1Id, Hand player1Hand,
                              Long player2Id, Hand player2Hand) {
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

        // じゃんけん明細を生成

        val detail1 = new JankenDetail(null, null, player1Id, player1Hand, player1Result);
        val detail2 = new JankenDetail(null, null, player2Id, player2Hand, player2Result);

        // じゃんけんを生成

        val playedAt = LocalDateTime.now();
        return new Janken(null, playedAt, detail1, detail2);
    }

    private Long id;
    private LocalDateTime playedAt;
    private JankenDetail detail1;
    private JankenDetail detail2;

    public List<JankenDetail> details() {
        return List.of(detail1, detail2);
    }

    public Optional<Long> winnerPlayerId() {
        if (detail1.isResultWin()) {
            return Optional.of(detail1.getPlayerId());

        } else if (detail2.isResultWin()) {
            return Optional.of(detail2.getPlayerId());

        } else {
            return Optional.empty();
        }
    }

}
