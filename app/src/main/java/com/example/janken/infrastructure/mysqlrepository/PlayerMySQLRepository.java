package com.example.janken.infrastructure.mysqlrepository;

import com.example.janken.domain.model.player.Player;
import com.example.janken.domain.model.player.PlayerRepository;
import com.example.janken.infrastructure.jooq.generated.tables.PLAYERS_TABLE;
import com.example.janken.infrastructure.jooq.generated.tables.records.PlayersRecord;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class PlayerMySQLRepository implements PlayerRepository {

    private static final PLAYERS_TABLE P = PLAYERS_TABLE.PLAYERS.as("p");

    private DSLContext db;

    @Override
    public List<Player> findAllOrderById() {
        val records = selectFrom()
                .orderBy(P.ID)
                .fetch()
                .into(PlayersRecord.class);

        return records2Models(records);
    }

    @Override
    public Optional<Player> findPlayerById(String playerId) {
        val records = selectFrom()
                .where(P.ID.eq(playerId))
                .fetch()
                .into(PlayersRecord.class);

        return records2Models(records).stream()
                .findFirst();
    }

    // private methods

    private SelectJoinStep<Record2<String, String>> selectFrom() {
        return db.select(P.ID, P.NAME)
                .from(P);
    }

    private List<Player> records2Models(List<PlayersRecord> records) {
        return records.stream()
                .map(this::record2Model)
                .collect(Collectors.toList());
    }

    private Player record2Model(PlayersRecord record) {
        return new Player(record.getId(), record.getName());
    }

}
