package com.example.janken.domain.model.janken;

import com.example.janken.domain.model.player.Player;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Janken {

    public static Janken play(String player1Id, Hand player1Hand,
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

    private static String generateId() {
        return UUID.randomUUID().toString();
    }

    private String id;
    private LocalDateTime playedAt;
    private JankenDetail detail1;
    private JankenDetail detail2;

    public List<JankenDetail> details() {
        return List.of(detail1, detail2);
    }

    public Optional<String> winnerPlayerId() {
        if (detail1.isResultWin()) {
            return Optional.of(detail1.getPlayerId());

        } else if (detail2.isResultWin()) {
            return Optional.of(detail2.getPlayerId());

        } else {
            return Optional.empty();
        }
    }

    /**
     * 引数のプレイヤーが勝者かを返却します。
     * <p>
     * プレイヤーがじゃんけんの参加者でなかった場合は勝者ではないので false を返します。
     */
    public boolean isWinner(Player player) {
        return details().stream()
                // そのプレイヤーのじゃんけん明細にしぼる
                .filter(d -> d.getPlayerId().equals(player.getId()))
                .findFirst()
                // 勝利かどうかに変換
                .map(JankenDetail::isResultWin)
                // 存在しなければ勝利ではないとする
                .orElse(false);
    }

}
