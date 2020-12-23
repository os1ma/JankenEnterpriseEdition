package com.example.janken.domain.model.janken;

import com.example.janken.domain.model.player.Player;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Janken {

    private String id;
    private LocalDateTime playedAt;
    List<JankenDetail> details;

    public List<String> winnerPlayerIds() {
        return details.stream()
                .filter(JankenDetail::isResultWin)
                .map(JankenDetail::playerId)
                .collect(Collectors.toList());
    }

    /**
     * 引数のプレイヤーが勝者かを返却します。
     * <p>
     * プレイヤーがじゃんけんの参加者でなかった場合は勝者ではないので false を返します。
     */
    public boolean isWinner(Player player) {
        return details.stream()
                // そのプレイヤーのじゃんけん明細にしぼる
                .filter(d -> d.playerIdEquals(player.getId()))
                .findFirst()
                // 勝利かどうかに変換
                .map(JankenDetail::isResultWin)
                // 存在しなければ勝利ではないとする
                .orElse(false);
    }

}
