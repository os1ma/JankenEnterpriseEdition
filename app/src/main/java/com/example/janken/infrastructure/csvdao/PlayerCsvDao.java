package com.example.janken.infrastructure.csvdao;

import com.example.janken.domain.dao.PlayerDao;
import com.example.janken.domain.model.Player;
import lombok.val;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PlayerCsvDao implements PlayerDao {

    private static final String PLAYERS_CSV = CsvDaoUtils.DATA_DIR + "players.csv";

    public Player findPlayerById(long playerId) {
        try (val stream = Files.lines(Paths.get(PLAYERS_CSV), StandardCharsets.UTF_8)) {
            return stream
                    .map(this::line2Player)
                    .filter(p -> p.getId() == playerId)
                    .findFirst()
                    .orElseThrow();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Player line2Player(String line) {
        val values = line.split(CsvDaoUtils.CSV_DELIMITER);
        val id = Long.parseLong(values[0]);
        val name = values[1];

        return new Player(id, name);
    }

}
