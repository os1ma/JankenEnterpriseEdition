package com.example.janken;

import com.example.janken.controller.JankenController;
import lombok.val;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        val controller = new JankenController();
        controller.play();
    }

}