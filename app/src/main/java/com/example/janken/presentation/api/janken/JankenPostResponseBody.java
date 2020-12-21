package com.example.janken.presentation.api.janken;

import com.example.janken.application.service.janken.JankenPlayOutput;
import com.example.janken.domain.model.player.Player;
import lombok.*;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
class JankenPostResponseBody {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static JankenPostResponseBody of(JankenPlayOutput output) {
        val winnerPlayerName = output.getMaybeWinner()
                .map(Player::getName)
                .orElse(null);
        return new JankenPostResponseBody(winnerPlayerName);
    }

    private String winnerPlayerName;
}
