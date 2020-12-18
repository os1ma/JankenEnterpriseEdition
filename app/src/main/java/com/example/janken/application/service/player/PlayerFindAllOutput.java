package com.example.janken.application.service.player;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class PlayerFindAllOutput {
    private List<PlayerFindAllOutputPlayer> players;
}
