package com.example.janken.application.service.player;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class PlayerFindAllOutputPlayer {
    private String id;
    private String name;
    private long winCount;
}
