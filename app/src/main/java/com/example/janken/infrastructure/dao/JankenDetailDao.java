package com.example.janken.infrastructure.dao;

import com.example.janken.domain.model.janken.JankenDetail;
import com.example.janken.domain.transaction.Transaction;

import java.util.List;
import java.util.Optional;

public interface JankenDetailDao {

    List<JankenDetail> findAllOrderById(Transaction tx);

    List<JankenDetail> findByJankenIdOrderById(Transaction tx, String jankenId);

    Optional<JankenDetail> findById(Transaction tx, String id);

    long count(Transaction tx);

    void insertAll(Transaction tx, List<JankenDetail> jankenDetails);

}
