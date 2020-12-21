package com.example.janken.infrastructure.dao;

import com.example.janken.domain.model.janken.JankenDetail;
import com.example.janken.domain.transaction.Transaction;

import java.util.List;
import java.util.Optional;

public interface JankenDetailDao<T extends Transaction> {

    List<JankenDetail> findAllOrderById(T tx);

    List<JankenDetail> findByJankenIdOrderById(T tx, String jankenId);

    Optional<JankenDetail> findById(T tx, String id);

    long count(T tx);

    void insertAll(T tx, List<JankenDetail> jankenDetails);

}
