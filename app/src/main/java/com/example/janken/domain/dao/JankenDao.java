package com.example.janken.domain.dao;

import com.example.janken.domain.model.Janken;

import java.util.Optional;

public interface JankenDao {

    Optional<Janken> findById(long id);

    long count();

    Janken insert(Janken janken);

}
