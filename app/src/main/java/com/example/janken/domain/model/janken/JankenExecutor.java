package com.example.janken.domain.model.janken;

import lombok.val;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JankenExecutor {

    public Janken play(List<HandSelection> handSelections) {
        val jankenId = generateId();
        val details = play(jankenId, handSelections);
        val playedAt = LocalDateTime.now();

        return new Janken(jankenId, playedAt, details);
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    private List<JankenDetail> play(String jankenId, List<HandSelection> handSelections) {
        // 人数が 1 人以下の場合はじゃんけんはできない
        if (handSelections.size() <= 1) {
            throw new IllegalArgumentException("handSelections size must not be less than 1. handSelections = " + handSelections);
        }

        val selectedHands = handSelections.stream()
                .map(HandSelection::getHand)
                .distinct()
                .collect(Collectors.toList());

        // 1 種類の手しか選ばれていないか全ての手が選ばれている場合はあいこ
        val selectedHandTypes = selectedHands.size();
        if (selectedHandTypes == 1 || selectedHandTypes == Hand.values().length) {
            return handSelections.stream()
                    .map(hs -> new JankenDetail(generateId(), jankenId, hs, Result.DRAW))
                    .collect(Collectors.toList());
        }

        // あいこではない場合
        val hand1 = selectedHands.get(0);
        val hand2 = selectedHands.get(1);
        val winningHand = hand1.wins(hand2) ? hand1 : hand2;

        return handSelections.stream()
                .map(hs -> {
                    val result = hs.getHand().equals(winningHand) ? Result.WIN : Result.LOSE;
                    return new JankenDetail(generateId(), jankenId, hs, result);
                })
                .collect(Collectors.toList());
    }

}
