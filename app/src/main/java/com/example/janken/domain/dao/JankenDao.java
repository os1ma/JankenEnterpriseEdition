package com.example.janken.domain.dao;

import com.example.janken.domain.model.Janken;
import com.example.janken.framework.Transaction;

import java.util.List;
import java.util.Optional;

public interface JankenDao {

    List<Janken> findAllOrderById(Transaction tx);

    Optional<Janken> findById(Transaction tx, long id);

    long count(Transaction tx);

    Janken insert(Transaction tx, Janken janken);

}
