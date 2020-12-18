package com.example.janken.presentation.api.player;

import com.example.janken.application.service.player.PlayerFindAllOutput;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
class PlayerListResponseBody {

    static PlayerListResponseBody of(PlayerFindAllOutput output) {
        val players = output.getPlayers().stream()
                .map(p -> new PlayerListResponseBodyPlayer(
                        p.getId(),
                        p.getName(),
                        p.getWinCount()))
                .collect(Collectors.toList());
        return new PlayerListResponseBody(players);
    }

    private List<PlayerListResponseBodyPlayer> players;
}

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
class PlayerListResponseBodyPlayer {
    private String id;
    private String name;
    private long winCount;
}
