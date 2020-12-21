package com.example.janken.presentation.cli.controller;

import com.example.janken.application.service.janken.JankenApplicationService;
import com.example.janken.application.service.player.PlayerApplicationService;
import com.example.janken.domain.model.janken.Hand;
import com.example.janken.presentation.cli.view.StandardOutputView;
import lombok.AllArgsConstructor;
import lombok.val;

@AllArgsConstructor
public class JankenEntrypointController implements CLIController {

    private JankenContext ctx;
    private PlayerApplicationService playerApplicationService;
    private JankenApplicationService jankenApplicationService;

    @Override
    public void handle(String input) {
        val playerId = ctx.getPlayer1Id();
        val player = playerApplicationService.findPlayerById(playerId).orElseThrow();

        new StandardOutputView(JankenContext.VIEW_RESOURCE_PREFIX + "scan-prompt.vm")
                .with("player", player)
                .with("hands", Hand.values())
                .next(new PostHandController(ctx, playerApplicationService, jankenApplicationService))
                .show();
    }

}
