package com.example.janken.application.query.player;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class PlayerListQueryModel {
    private List<PlayerListQueryModelPlayer> players;
}
