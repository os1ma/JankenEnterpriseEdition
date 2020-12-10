package com.example.janken.domain.transaction;

import java.util.function.Consumer;
import java.util.function.Function;

public interface TransactionManager {

    /**
     * 以下の流れで戻り値のあるトランザクション処理を実行します。
     *
     * <ol>
     *     <li>トランザクションを開始</li>
     *     <li>引数の処理を実行</li>
     *     <li>コミットを実行し、トランザクションを終了</li>
     *     <li>結果を返却</li>
     * </ol>
     * <p>
     * 引数の処理の途中で例外が発生した場合は、コミットせずにトランザクションを終了します。
     */
    <U> U transactional(Function<Transaction, U> f);

    /**
     * 以下の流れで戻り値のないトランザクション処理を実行します。
     *
     * <ol>
     *     <li>トランザクションを開始</li>
     *     <li>引数の処理を実行</li>
     *     <li>コミットを実行し、トランザクションを終了</li>
     * </ol>
     * <p>
     * 引数の処理の途中で例外が発生した場合は、コミットせずにトランザクションを終了します。
     */
    void transactional(Consumer<Transaction> c);

}
