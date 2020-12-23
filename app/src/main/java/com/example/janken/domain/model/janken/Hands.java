package com.example.janken.domain.model.janken;

import lombok.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Hands {

    private List<Hand> list;

    public boolean isDraw() {
        return distinctedHandList().size() != 2;
    }

    public Hand winningHand() {
        val distinctedHandList = distinctedHandList();

        if (isDraw()) {
            throw new NoSuchElementException("Winning hand not exist. list = " + list);
        }

        val hand1 = distinctedHandList.get(0);
        val hand2 = distinctedHandList.get(1);
        return hand1.wins(hand2) ? hand1 : hand2;
    }

    private List<Hand> distinctedHandList() {
        return list.stream().distinct().collect(Collectors.toList());
    }

}
