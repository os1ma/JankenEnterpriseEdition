package com.example.janken.infrastructure.mysqlquery;

import com.example.janken.application.query.PlayerListQueryModel;
import com.example.janken.application.query.PlayerListQueryModelPlayer;
import com.example.janken.application.query.PlayerQueryService;
import com.example.janken.domain.model.janken.Result;
import com.example.janken.infrastructure.jooq.generated.janken.tables.JANKEN_DETAILS_TABLE;
import com.example.janken.infrastructure.jooq.generated.janken.tables.PLAYERS_TABLE;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jooq.DSLContext;
import org.jooq.types.UInteger;
import org.springframework.stereotype.Repository;

import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;

@Repository
@AllArgsConstructor
public class PlayerMySQLQueryService implements PlayerQueryService {

    private static final PLAYERS_TABLE P = PLAYERS_TABLE.PLAYERS.as("p");
    private static final JANKEN_DETAILS_TABLE JD = JANKEN_DETAILS_TABLE.JANKEN_DETAILS.as("jd");

    private DSLContext db;

    /**
     * select
     *     `p`.`id`,
     *     `p`.`name`,
     *     coalesce(`pw`.`win_count`, ?)
     * from `janken`.`players` as `p`
     * left outer join (
     *     select
     *         max(`p`.`id`) as `player_id`,
     *         count(*) as `win_count`
     *     from `janken`.`players` as `p`
     *     join `janken`.`janken_details` as `jd`
     *     on `p`.`id` = `jd`.`player_id`
     *     where `jd`.`result` = ?
     * ) as `pw`
     * on `p`.`id` = `pw`.`player_id`
     */
    @Override
    public PlayerListQueryModel queryAll() {
        val winValue = UInteger.valueOf(Result.WIN.getValue());

        val pw = db.select(
                max(P.ID).as("player_id"),
                count().as("win_count"))
                .from(P)
                .innerJoin(JD)
                .on(P.ID.eq(JD.PLAYER_ID))
                .where(JD.RESULT.eq(winValue))
                .asTable("pw");

        val players = db.select(
                P.ID,
                P.NAME,
                coalesce(pw.field("win_count"), 0))
                .from(P)
                .leftJoin(pw)
                .on(P.ID.eq(pw.field(0, String.class)))
                .fetch()
                .stream()
                .map(r -> new PlayerListQueryModelPlayer(
                        r.get(0, String.class),
                        r.get(1, String.class),
                        r.get(2, Long.class)))
                .collect(Collectors.toList());

        return new PlayerListQueryModel(players);
    }

}
