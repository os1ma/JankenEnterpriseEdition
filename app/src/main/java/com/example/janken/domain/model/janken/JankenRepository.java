package com.example.janken.domain.model.janken;

import com.example.janken.domain.transaction.Transaction;

import java.util.List;
import java.util.Optional;

public interface JankenRepository {

    List<Janken> findAllOrderById(Transaction tx);

    Optional<Janken> findById(Transaction tx, long id);

    long count(Transaction tx);

    Janken save(Transaction tx, Janken janken);

}
