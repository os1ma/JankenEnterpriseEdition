package com.example.janken.application.service.janken;

import com.example.janken.application.exception.ApplicationException;
import com.example.janken.domain.model.janken.*;
import com.example.janken.domain.model.player.Player;
import com.example.janken.domain.model.player.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class JankenApplicationService {

    private static final String PLAYER_NOT_EXIST_MESSAGE = "Player not exist";

    private JankenRepository jankenRepository;
    private PlayerRepository playerRepository;
    private JankenExecutor jankenExecutor;

    /**
     * じゃんけんを実行し、結果を保存して、勝者を返します。
     */
    public Optional<Player> play(String player1Id, Hand player1Hand,
                                 String player2Id, Hand player2Hand) {

        // プレイヤーの存在チェック
        playerRepository.findPlayerById(player1Id)
                .orElseThrow(() -> new ApplicationException(HttpStatus.BAD_REQUEST, PLAYER_NOT_EXIST_MESSAGE));
        playerRepository.findPlayerById(player2Id)
                .orElseThrow(() -> new ApplicationException(HttpStatus.BAD_REQUEST, PLAYER_NOT_EXIST_MESSAGE));

        // 手の準備
        val handSelection1 = new HandSelection(player1Id, player1Hand);
        val handSelection2 = new HandSelection(player2Id, player2Hand);
        val handSelections = HandSelections.of(handSelection1, handSelection2);

        // じゃんけんを実行
        val janken = jankenExecutor.play(handSelections);

        // 保存
        jankenRepository.save(janken);

        // 勝者を返却
        return janken.winnerPlayerIds()
                .stream()
                .findFirst()
                .map(playerId -> playerRepository.findPlayerById(playerId).orElseThrow());
    }

}
