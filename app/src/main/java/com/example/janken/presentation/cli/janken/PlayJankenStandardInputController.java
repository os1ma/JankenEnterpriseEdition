package com.example.janken.presentation.cli.janken;

import com.example.janken.application.scenario.PlayJankenInputPort;

import java.util.Scanner;

public class PlayJankenStandardInputController implements PlayJankenInputPort {

    private static final Scanner STDIN_SCANNER = new Scanner(System.in);

    @Override
    public String getInputStr() {
        return STDIN_SCANNER.nextLine();
    }

}


