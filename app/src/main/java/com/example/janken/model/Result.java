package com.example.janken.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Result {
    WIN(0),
    LOSE(1),
    DRAW(2);

    public static Result of(int value) {
        return Arrays.stream(Result.values())
                .filter(r -> r.getValue() == value)
                .findFirst()
                .orElseThrow();
    }

    private int value;
}
