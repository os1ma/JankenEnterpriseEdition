package com.example.janken.domain.model.janken;

import com.example.janken.domain.model.player.Player;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Janken {

    private String id;
    private LocalDateTime playedAt;
    private JankenDetail detail1;
    private JankenDetail detail2;

    public List<JankenDetail> details() {
        return List.of(detail1, detail2);
    }

    public Optional<String> winnerPlayerId() {
        if (detail1.isResultWin()) {
            return Optional.of(detail1.getHandSelection().getPlayerId());

        } else if (detail2.isResultWin()) {
            return Optional.of(detail2.getHandSelection().getPlayerId());

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
                .filter(d -> d.playerIdEquals(player.getId()))
                .findFirst()
                // 勝利かどうかに変換
                .map(JankenDetail::isResultWin)
                // 存在しなければ勝利ではないとする
                .orElse(false);
    }

}
