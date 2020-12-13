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
    private String playerId;
    private Hand hand;
    private Result result;

    public boolean isResultWin() {
        return result.equals(Result.WIN);
    }

}
