package com.example.janken.businesslogic.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Hand {
    STONE(0, "STONE"),
    PAPER(1, "PAPER"),
    SCISSORS(2, "SCISSORS");

    public static Hand of(int value) {
        return Arrays.stream(Hand.values())
                .filter(h -> h.getValue() == value)
                .findFirst()
                .orElseThrow();
    }

    private int value;
    private String name;
}
