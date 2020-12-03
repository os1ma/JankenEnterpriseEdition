package com.example.janken.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Result {
    WIN(0),
    LOSE(1),
    DRAW(2);

    private int value;
}
