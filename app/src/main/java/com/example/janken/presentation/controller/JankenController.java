package com.example.janken.presentation.controller;

import com.example.janken.application.service.JankenService;
import com.example.janken.application.service.PlayerService;
import com.example.janken.domain.model.Hand;
import com.example.janken.domain.model.Player;
import com.example.janken.framework.ServiceLocator;
import com.example.janken.framework.View;
import lombok.val;

import java.util.Arrays;
import java.util.Scanner;

public class JankenController {

    // ID は実際のアプリケーションでは認証情報から取得することが想定される
    private static final long PLAYER_1_ID = 1;
    private static final long PLAYER_2_ID = 2;

    private static final Scanner STDIN_SCANNER = new Scanner(System.in);
    private static final String VIEW_RESOURCE_PREFIX = "view/";

    private PlayerService playerService = ServiceLocator.resolve(PlayerService.class);
    private JankenService jankenService = ServiceLocator.resolve(JankenService.class);

    public void play() {
        val player1 = playerService.findPlayerById(PLAYER_1_ID);
        val player2 = playerService.findPlayerById(PLAYER_2_ID);

        val player1Hand = scanHand(player1);
        val player2Hand = scanHand(player2);

        showHandWithName(player1Hand, player1);
        showHandWithName(player2Hand, player2);

        val maybeWinner = jankenService.play(player1, player1Hand, player2, player2Hand);

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
