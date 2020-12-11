package com.example.janken.domain.model;

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

    public boolean wins(Hand other) {
        switch (this) {
            case STONE:
                return other.equals(SCISSORS);
            case PAPER:
                return other.equals(STONE);
            case SCISSORS:
                return other.equals(PAPER);
            default:
                throw new IllegalStateException("Invalid hand. this = " + this);
        }
    }

}
