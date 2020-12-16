package com.example.janken.infrastructure.mysqlrepository;

import com.example.janken.domain.model.janken.Janken;
import com.example.janken.domain.model.janken.JankenRepository;
import com.example.janken.domain.transaction.Transaction;
import com.example.janken.infrastructure.dao.JankenDao;
import com.example.janken.infrastructure.dao.JankenDetailDao;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class JankenMySQLRepository implements JankenRepository {

    private JankenDao jankenDao;
    private JankenDetailDao jankenDetailDao;

    @Override
    public List<Janken> findAllOrderByPlayedAt(Transaction tx) {
        val jankenWithoutDetails = jankenDao.findAllOrderByPlayedAt(tx);
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
    public Optional<Janken> findById(Transaction tx, String id) {
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
    public void save(Transaction tx, Janken janken) {
        jankenDao.insert(tx, janken);
        jankenDetailDao.insertAll(tx, janken.details());
    }

}
