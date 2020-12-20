package com.example.janken.domain.model.janken;

import java.util.List;
import java.util.Optional;

public interface JankenRepository {

    List<Janken> findAll();

    List<Janken> findAllOrderByPlayedAt();

    Optional<Janken> findById(String id);

    long count();

    void save(Janken janken);

}
