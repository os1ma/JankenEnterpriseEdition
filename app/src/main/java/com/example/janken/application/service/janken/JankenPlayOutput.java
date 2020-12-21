package com.example.janken.application.service.janken;

import com.example.janken.domain.model.player.Player;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Optional;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class JankenPlayOutput {
    private Player player1;
    private Player player2;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Player> maybeWinner;
}
