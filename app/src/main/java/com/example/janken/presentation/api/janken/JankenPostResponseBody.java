package com.example.janken.presentation.api.janken;

import com.example.janken.domain.model.player.Player;
import lombok.*;

import java.util.Optional;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
class JankenPostResponseBody {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static JankenPostResponseBody of(Optional<Player> maybeWinner) {
        val winnerPlayerName = maybeWinner.map(Player::getName).orElse(null);
        return new JankenPostResponseBody(winnerPlayerName);
    }

    private String winnerPlayerName;
}
