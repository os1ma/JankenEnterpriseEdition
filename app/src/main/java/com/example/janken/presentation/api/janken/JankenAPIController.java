package com.example.janken.presentation.api.janken;

import com.example.janken.application.service.janken.JankenApplicationService;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/jankens")
@AllArgsConstructor
public class JankenAPIController {

    private JankenApplicationService service;

    @PostMapping
    public JankenPostResponseBody post(@RequestBody @Validated JankenPostRequestBody requestBody) {

        val output = service.play(
                requestBody.getPlayer1Id(),
                requestBody.player1Hand(),
                requestBody.getPlayer2Id(),
                requestBody.player2Hand());

        return JankenPostResponseBody.of(output);
    }

}
