package com.example.janken.application.scenario;

import com.example.janken.domain.model.janken.Hand;
import com.example.janken.domain.model.player.Player;

import java.util.Optional;

public interface PlayJankenOutputPort {

    void showScanPrompt(Player player);

    void showInvalidInput(String input);

    void showHandWithName(Hand hand, Player player);

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    void showResult(Optional<Player> maybeWinner);

}
