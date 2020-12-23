package com.example.janken.domain.model.janken;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class HandSelection {

    private String playerId;
    private Hand hand;

    public boolean wins(HandSelection other) {
        return hand.wins(other.hand);
    }

}
