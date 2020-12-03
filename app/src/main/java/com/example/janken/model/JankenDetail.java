package com.example.janken.model;

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
}