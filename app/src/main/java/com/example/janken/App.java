package com.example.janken;

import com.example.janken.controller.JankenController;
import lombok.val;

public class App {

    public static void main(String[] args) {
        val controller = new JankenController();
        controller.play();
    }

}