package com.example.janken.infrastructure.dao;

import com.example.janken.domain.model.janken.Janken;
import com.example.janken.domain.transaction.Transaction;

import java.util.List;
import java.util.Optional;

public interface JankenDao<T extends Transaction> {

    List<Janken> findAllOrderByPlayedAt(T tx);

    Optional<Janken> findById(T tx, String id);

    long count(T tx);

    void insert(T tx, Janken janken);

}
