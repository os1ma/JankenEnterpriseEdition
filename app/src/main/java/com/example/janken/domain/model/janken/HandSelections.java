package com.example.janken.domain.model.janken;

import lombok.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class HandSelections {

    public static HandSelections of(HandSelection... elements) {
        return new HandSelections(List.of(elements));
    }

    private List<HandSelection> list;

    public boolean existEnoughToPlayJanken() {
        return list.size() >= 2;
    }

    public Hands hands() {
        val handList = list.stream()
                .map(HandSelection::getHand)
                .collect(Collectors.toList());
        return new Hands(handList);
    }

    public <T> List<T> map(Function<HandSelection, ? extends T> f) {
        return list.stream()
                .map(f)
                .collect(Collectors.toList());
    }

    public boolean isDraw() {
        return hands().isDraw();
    }

    public Hand winningHand() {
        return hands().winningHand();
    }

}
