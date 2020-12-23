package com.example.janken.domain.model.janken;

import lombok.val;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class JankenExecutor {

    public Janken play(HandSelections handSelections) {
        val jankenId = generateId();
        val details = play(jankenId, handSelections);
        val playedAt = LocalDateTime.now();

        return new Janken(jankenId, playedAt, details);
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    private List<JankenDetail> play(String jankenId, HandSelections handSelections) {
        if (!handSelections.existEnoughToPlayJanken()) {
            throw new IllegalArgumentException("handSelections does not have enough elements. handSelections = " + handSelections);
        }

        if (handSelections.isDraw()) {
            return handSelections.map(hs -> new JankenDetail(generateId(), jankenId, hs, Result.DRAW));

        } else {
            val winningHand = handSelections.winningHand();
            return handSelections.map(hs -> {
                val result = hs.getHand().equals(winningHand) ? Result.WIN : Result.LOSE;
                return new JankenDetail(generateId(), jankenId, hs, result);
            });
        }

    }

}
