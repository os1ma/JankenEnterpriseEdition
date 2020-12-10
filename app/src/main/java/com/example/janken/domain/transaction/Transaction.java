package com.example.janken.domain.transaction;

public interface Transaction extends AutoCloseable {

    void commit();

    void close();

}
