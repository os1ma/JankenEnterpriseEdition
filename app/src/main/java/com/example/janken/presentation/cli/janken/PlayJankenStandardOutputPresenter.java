package com.example.janken.presentation.cli.janken;

import com.example.janken.application.scenario.PlayJankenOutputPort;
import com.example.janken.domain.model.janken.Hand;
import com.example.janken.domain.model.player.Player;
import com.example.janken.presentation.cli.view.StandardOutputView;

import java.util.Optional;

public class PlayJankenStandardOutputPresenter implements PlayJankenOutputPort {

    private static final String VIEW_RESOURCE_PREFIX = "cli/view/";

    @Override
    public void showScanPrompt(Player player) {
        new StandardOutputView(VIEW_RESOURCE_PREFIX + "scan-prompt.vm")
                .with("player", player)
                .with("hands", Hand.values())
                .show();
    }

    @Override
    public void showInvalidInput(String input) {
        new StandardOutputView(VIEW_RESOURCE_PREFIX + "invalid-input.vm")
                .with("input", input)
                .show();
    }

    public void showHandWithName(Hand hand, Player player) {
        new StandardOutputView(VIEW_RESOURCE_PREFIX + "show-hand.vm")
                .with("player", player)
                .with("hand", hand)
                .show();
    }

    @Override
    public void showResult(Optional<Player> maybeWinner) {
        new StandardOutputView(VIEW_RESOURCE_PREFIX + "result.vm")
                .with("winner", maybeWinner.orElse(null))
                .show();
    }

}
