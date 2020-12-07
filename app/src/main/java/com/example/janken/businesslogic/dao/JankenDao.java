package com.example.janken.businesslogic.dao;

import com.example.janken.businesslogic.model.Janken;

import java.util.Optional;

public interface JankenDao {

    Optional<Janken> findById(long id);

    long count();

    Janken insert(Janken janken);

}
