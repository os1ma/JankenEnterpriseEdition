package com.example.janken.infrastructure.mysqlrepository;

import com.example.janken.domain.model.player.Player;
import com.example.janken.domain.model.player.PlayerRepository;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.infrastructure.dao.PlayerDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class PlayerMySQLRepository implements PlayerRepository {

    private PlayerDao playerDao;

    @Override
    public Player findPlayerById(Transaction tx, String playerId) {
        return playerDao.findPlayerById(tx, playerId);
    }

}
