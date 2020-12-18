package com.example.janken.application.query.player;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class PlayerListQueryModelPlayer {
    private String id;
    private String name;
    private long winCount;
}
