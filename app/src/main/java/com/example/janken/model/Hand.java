package com.example.janken.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Hand {
    STONE(0, "STONE"),
    PAPER(1, "PAPER"),
    SCISSORS(2, "SCISSORS");

    private int value;
    private String name;
}
