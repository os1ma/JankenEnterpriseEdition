package com.example.janken.infrastructure.mysqlrepository;

import com.example.janken.infrastructure.dao.JankenDao;
import com.example.janken.infrastructure.dao.JankenDetailDao;
import com.example.janken.domain.model.janken.Janken;
import com.example.janken.domain.model.janken.JankenRepository;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.registry.ServiceLocator;
import lombok.val;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JankenMySQLRepository implements JankenRepository {

    private JankenDao jankenDao = ServiceLocator.resolve(JankenDao.class);
    private JankenDetailDao jankenDetailDao = ServiceLocator.resolve(JankenDetailDao.class);

    @Override
    public List<Janken> findAllOrderById(Transaction tx) {
        val jankenWithoutDetails = jankenDao.findAllOrderById(tx);
        val jankenDetails = jankenDetailDao.findAllOrderById(tx);

        return jankenWithoutDetails.stream()
                .map(j -> {
                    val jankenDetailsFilteredById = jankenDetails.stream()
                            .filter(jd -> jd.getJankenId().equals(j.getId()))
                            .collect(Collectors.toList());

                    return new Janken(
                            j.getId(),
                            j.getPlayedAt(),
                            jankenDetailsFilteredById.get(0),
                            jankenDetailsFilteredById.get(1));
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Janken> findById(Transaction tx, long id) {
        return jankenDao.findById(tx, id)
                .map(j -> {
                    val jankenDetails = jankenDetailDao.findByJankenIdOrderById(tx, id);

                    return new Janken(
                            j.getId(),
                            j.getPlayedAt(),
                            jankenDetails.get(0),
                            jankenDetails.get(1));
                });
    }

    @Override
    public long count(Transaction tx) {
        return jankenDao.count(tx);
    }

    @Override
    public Janken save(Transaction tx, Janken janken) {
        val jankenWithId = jankenDao.insert(tx, janken);
        val jankenDetailsWithId = jankenDetailDao.insertAll(tx, jankenWithId.details());

        return new Janken(
                jankenWithId.getId(),
                janken.getPlayedAt(),
                jankenDetailsWithId.get(0),
                jankenDetailsWithId.get(1));
    }

}
