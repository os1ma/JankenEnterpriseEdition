package com.example.janken.presentation.cli.controller;

import com.example.janken.application.service.JankenApplicationService;
import com.example.janken.application.service.PlayerApplicationService;
import com.example.janken.domain.model.janken.Hand;
import com.example.janken.domain.model.player.Player;
import com.example.janken.presentation.cli.view.View;
import com.example.janken.registry.ServiceLocator;
import lombok.val;

import java.util.Arrays;
import java.util.Scanner;

public class JankenController {

    // ID は実際のアプリケーションでは認証情報から取得することが想定される
    private static final String PLAYER_1_ID = "1";
    private static final String PLAYER_2_ID = "2";

    private static final Scanner STDIN_SCANNER = new Scanner(System.in);
    private static final String VIEW_RESOURCE_PREFIX = "cli/view/";

    private PlayerApplicationService playerApplicationService = ServiceLocator.resolve(PlayerApplicationService.class);
    private JankenApplicationService jankenApplicationService = ServiceLocator.resolve(JankenApplicationService.class);

    public void play() {
        val player1 = playerApplicationService.findPlayerById(PLAYER_1_ID);
        val player2 = playerApplicationService.findPlayerById(PLAYER_2_ID);

        val player1Hand = scanHand(player1);
        val player2Hand = scanHand(player2);

        showHandWithName(player1Hand, player1);
        showHandWithName(player2Hand, player2);

        val maybeWinner = jankenApplicationService.play(PLAYER_1_ID, player1Hand, PLAYER_2_ID, player2Hand);

        new View(VIEW_RESOURCE_PREFIX + "result.vm")
                .with("winner", maybeWinner.orElse(null))
                .show();
    }

    private static Hand scanHand(Player player) {
        while (true) {
            new View(VIEW_RESOURCE_PREFIX + "scan-prompt.vm")
                    .with("player", player)
                    .with("hands", Hand.values())
                    .show();

            val inputStr = STDIN_SCANNER.nextLine();

            val maybeHand = Arrays.stream(Hand.values())
                    .filter(hand -> {
                        val handValueStr = String.valueOf(hand.getValue());
                        return handValueStr.equals(inputStr);
                    })
                    .findFirst();

            if (maybeHand.isPresent()) {
                return maybeHand.get();
            } else {
                new View(VIEW_RESOURCE_PREFIX + "invalid-input.vm")
                        .with("input", inputStr)
                        .show();
            }
        }
    }

    private static void showHandWithName(Hand hand, Player player) {
        new View(VIEW_RESOURCE_PREFIX + "show-hand.vm")
                .with("player", player)
                .with("hand", hand)
                .show();
    }

}
