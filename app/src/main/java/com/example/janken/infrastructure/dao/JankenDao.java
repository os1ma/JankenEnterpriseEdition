package com.example.janken.infrastructure.dao;

import com.example.janken.domain.model.janken.Janken;
import com.example.janken.domain.transaction.Transaction;

import java.util.List;
import java.util.Optional;

public interface JankenDao {

    List<Janken> findAllOrderByPlayedAt(Transaction tx);

    Optional<Janken> findById(Transaction tx, String id);

    long count(Transaction tx);

    void insert(Transaction tx, Janken janken);

}
