package com.example.janken.domain.model.janken;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class JankenDetail {
    private String id;
    private String jankenId;
    private HandSelection handSelection;
    private Result result;

    public boolean isResultWin() {
        return result.equals(Result.WIN);
    }

    public String playerId() {
        return handSelection.getPlayerId();
    }

    public boolean playerIdEquals(String otherPlayerId) {
        return handSelection.getPlayerId().equals(otherPlayerId);
    }

}
