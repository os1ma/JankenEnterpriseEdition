package com.example.janken.application.scenario;

import com.example.janken.application.service.janken.JankenApplicationService;
import com.example.janken.application.service.player.PlayerApplicationService;
import com.example.janken.domain.model.janken.Hand;
import com.example.janken.domain.model.player.Player;
import lombok.AllArgsConstructor;
import lombok.val;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Optional;

@AllArgsConstructor
public class PlayJankenScenario {

    // ID は実際のアプリケーションでは認証情報から取得することが想定される
    private static final String PLAYER_1_ID = "1";
    private static final String PLAYER_2_ID = "2";

    private PlayJankenInputPort inputPort;
    private PlayJankenOutputPort outputPort;

    private PlayerApplicationService playerApplicationService;
    private JankenApplicationService jankenApplicationService;

    public void run() {
        val player1 = playerApplicationService.findPlayerById(PLAYER_1_ID).orElseThrow();
        val player2 = playerApplicationService.findPlayerById(PLAYER_2_ID).orElseThrow();

        val player1Hand = getHandUntilSuccess(player1);
        val player2Hand = getHandUntilSuccess(player2);

        outputPort.showHandWithName(player1Hand, player1);
        outputPort.showHandWithName(player2Hand, player2);

        val maybeWinner = jankenApplicationService.play(PLAYER_1_ID, player1Hand, PLAYER_2_ID, player2Hand);

        outputPort.showResult(maybeWinner);
    }

    private Hand getHandUntilSuccess(Player player) {
        while (true) {
            val maybeHand = getHand(player);

            if (maybeHand.isPresent()) {
                return maybeHand.get();
            }
        }
    }

    private Optional<Hand> getHand(Player player) {
        outputPort.showScanPrompt(player);

        val inputStr = inputPort.getInputStr();

        // 数値ではない場合
        if (!NumberUtils.isDigits(inputStr)) {
            outputPort.showInvalidInput(inputStr);
            return Optional.empty();
        }

        val inputValue = Integer.valueOf(inputStr);

        // 不正な数値の場合
        if (!Hand.isValid(inputValue)) {
            outputPort.showInvalidInput(inputStr);
            return Optional.empty();
        }

        return Optional.of(Hand.of(inputValue));
    }

}
