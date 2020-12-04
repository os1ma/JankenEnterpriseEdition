package com.example.janken.service;

import com.example.janken.model.Player;
import lombok.val;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PlayerService {

    private static final String PLAYERS_CSV = ServiceConfigurations.DATA_DIR + "players.csv";

    public Player findPlayerById(long playerId) throws IOException {
        try (val stream = Files.lines(Paths.get(PLAYERS_CSV), StandardCharsets.UTF_8)) {
            return stream
                    .map(line -> {
                        val values = line.split(ServiceConfigurations.CSV_DELIMITER);
                        val id = Long.parseLong(values[0]);
                        val name = values[1];
                        return new Player(id, name);
                    })
                    // ID で検索
                    .filter(p -> p.getId() == playerId)
                    .findFirst()
                    .orElseThrow(() -> {
                        throw new IllegalArgumentException("Player not exist. playerId = " + playerId);
                    });
        }
    }

}
