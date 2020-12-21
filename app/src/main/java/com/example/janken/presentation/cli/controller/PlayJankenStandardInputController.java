package com.example.janken.presentation.cli.controller;

import com.example.janken.application.scenario.PlayJankenInputPort;
import lombok.AllArgsConstructor;

import java.util.Scanner;

@AllArgsConstructor
public class PlayJankenStandardInputController implements PlayJankenInputPort {

    private static final Scanner STDIN_SCANNER = new Scanner(System.in);

    @Override
    public String getInputStr() {
        return STDIN_SCANNER.nextLine();
    }
}


