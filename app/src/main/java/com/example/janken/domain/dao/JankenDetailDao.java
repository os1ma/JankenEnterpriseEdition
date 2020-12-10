package com.example.janken.domain.dao;

import com.example.janken.domain.model.JankenDetail;
import com.example.janken.domain.transaction.Transaction;

import java.util.List;
import java.util.Optional;

public interface JankenDetailDao {

    List<JankenDetail> findAllOrderById(Transaction tx);

    Optional<JankenDetail> findById(Transaction tx, long id);

    long count(Transaction tx);

    List<JankenDetail> insertAll(Transaction tx, List<JankenDetail> jankenDetails);

}
