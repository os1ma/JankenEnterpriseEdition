package com.example.janken.infrastructure.dao;

import com.example.janken.domain.model.janken.Janken;
import com.example.janken.domain.transaction.Transaction;

import java.util.List;
import java.util.Optional;

public interface JankenDao {

    List<Janken> findAllOrderById(Transaction tx);

    Optional<Janken> findById(Transaction tx, long id);

    long count(Transaction tx);

    Janken insert(Transaction tx, Janken janken);

}
