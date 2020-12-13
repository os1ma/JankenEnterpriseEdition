package com.example.janken.domain.model.janken;

import com.example.janken.domain.transaction.Transaction;

import java.util.List;
import java.util.Optional;

public interface JankenRepository {

    List<Janken> findAllOrderByPlayedAt(Transaction tx);

    Optional<Janken> findById(Transaction tx, String id);

    long count(Transaction tx);

    void save(Transaction tx, Janken janken);

}
