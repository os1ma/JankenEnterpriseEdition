package com.example.janken.presentation.cli.controller;

import com.example.janken.domain.model.janken.Hand;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class JankenContext {

    static final String VIEW_RESOURCE_PREFIX = "cli/view/";

    private String player1Id;
    private String player2Id;
    private Hand player1Hand;
    private Hand player2Hand;

}
