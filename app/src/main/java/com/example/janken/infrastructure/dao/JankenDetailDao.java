package com.example.janken.infrastructure.dao;

import com.example.janken.domain.model.janken.JankenDetail;
import com.example.janken.domain.transaction.Transaction;

import java.util.List;
import java.util.Optional;

public interface JankenDetailDao {

    List<JankenDetail> findAllOrderById(Transaction tx);

    List<JankenDetail> findByJankenIdOrderById(Transaction tx, long jankenId);

    Optional<JankenDetail> findById(Transaction tx, long id);

    long count(Transaction tx);

    List<JankenDetail> insertAll(Transaction tx, List<JankenDetail> jankenDetails);

}
