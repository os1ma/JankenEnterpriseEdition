package com.example.janken.presentation.api.player;

import com.example.janken.application.service.player.PlayerApplicationService;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/players")
@AllArgsConstructor
public class PlayerAPIController {

    private PlayerApplicationService service;

    @GetMapping
    public PlayerListResponseBody list() {
        val output = service.findAll();
        return PlayerListResponseBody.of(output);
    }

}
