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
    private Long id;
    private Long jankenId;
    private Long playerId;
    private Hand hand;
    private Result result;

    public boolean isResultWin() {
        return result.equals(Result.WIN);
    }

}
