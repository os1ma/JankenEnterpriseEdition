package com.example.janken.presentation.cli.controller;

import com.example.janken.application.service.janken.JankenApplicationService;
import com.example.janken.application.service.player.PlayerApplicationService;
import com.example.janken.domain.model.janken.Hand;
import com.example.janken.presentation.cli.view.StandardOutputView;
import lombok.AllArgsConstructor;
import lombok.val;
import org.apache.commons.lang.math.NumberUtils;

@AllArgsConstructor
public class PostHandController implements CLIController {

    private JankenContext ctx;
    private PlayerApplicationService playerApplicationService;
    private JankenApplicationService jankenApplicationService;

    @Override
    public void handle(String input) {
        // 入力が数値でない場合
        if (!NumberUtils.isDigits(input)) {
            showInvalidInputView(input);
            return;
        }

        val inputValue = Integer.valueOf(input);

        // 入力が不正な数値の場合
        if (!Hand.isValid(inputValue)) {
            showInvalidInputView(input);
            return;
        }

        val hand = Hand.of(inputValue);

        if (ctx.getPlayer1Hand() == null) {
            // 1 人目の入力の場合
            val player = playerApplicationService.findPlayerById(ctx.getPlayer2Id()).orElseThrow();

            val newCtx = new JankenContext(ctx.getPlayer1Id(), ctx.getPlayer2Id(), hand, null);
            new StandardOutputView(JankenContext.VIEW_RESOURCE_PREFIX + "scan-prompt.vm")
                    .with("player", player)
                    .with("hands", Hand.values())
                    .next(new PostHandController(newCtx, playerApplicationService, jankenApplicationService))
                    .show();

        } else {
            // 2 人目の入力の場合
            val output = jankenApplicationService.play(
                    ctx.getPlayer1Id(),
                    ctx.getPlayer1Hand(),
                    ctx.getPlayer2Id(),
                    hand);

            new StandardOutputView(JankenContext.VIEW_RESOURCE_PREFIX + "result.vm")
                    .with("player1Name", output.getPlayer1().getName())
                    .with("player1Hand", ctx.getPlayer1Hand())
                    .with("player2Name", output.getPlayer2().getName())
                    .with("player2Hand", hand)
                    .with("winner", output.getMaybeWinner().orElse(null))
                    .show();
        }
    }

    private void showInvalidInputView(String input) {
        val playerId = ctx.getPlayer1Hand() == null ? ctx.getPlayer1Id() : ctx.getPlayer2Id();
        val player = playerApplicationService.findPlayerById(playerId).orElseThrow();

        new StandardOutputView(JankenContext.VIEW_RESOURCE_PREFIX + "invalid-input.vm")
                .with("input", input)
                .with("hands", Hand.values())
                .with("player", player)
                .next(this)
                .show();
    }

}
