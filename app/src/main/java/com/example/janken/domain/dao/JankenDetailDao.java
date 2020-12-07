package com.example.janken.domain.dao;

import com.example.janken.domain.model.JankenDetail;

import java.util.List;
import java.util.Optional;

public interface JankenDetailDao {

    Optional<JankenDetail> findById(long id);

    long count();

    List<JankenDetail> insertAll(List<JankenDetail> jankenDetails);

}
