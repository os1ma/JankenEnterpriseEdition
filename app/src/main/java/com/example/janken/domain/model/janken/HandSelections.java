package com.example.janken.domain.model.janken;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

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

    public List<Hand> hands() {
        return list.stream()
                .map(HandSelection::getHand)
                .distinct()
                .collect(Collectors.toList());
    }

    public <T> List<T> map(Function<HandSelection, ? extends T> f) {
        return list.stream()
                .map(f)
                .collect(Collectors.toList());
    }

}
