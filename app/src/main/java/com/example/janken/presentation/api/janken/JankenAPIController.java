package com.example.janken.presentation.api.janken;

import com.example.janken.application.service.JankenApplicationService;
import lombok.AllArgsConstructor;
import lombok.val;
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
    public JankenPostResponseBody post(@RequestBody JankenPostRequestBody requestBody) {

        val maybeWinner = service.play(
                requestBody.getPlayer1Id(),
                requestBody.player1Hand(),
                requestBody.getPlayer2Id(),
                requestBody.player2Hand());

        return JankenPostResponseBody.of(maybeWinner);
    }

}
