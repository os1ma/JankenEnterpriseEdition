package com.example.janken.infrastructure.jdbctransaction;

public interface InsertMapper<T> {

    /**
     * {@link SimpleJDBCWrapper#insertAndReturnWithKeys} または {@link SimpleJDBCWrapper#insertOneAndReturnWithKey} の実行時に
     * INSERT のパラメータをオブジェクトから設定するための配列を生成する処理です。
     */
    Object[] object2InsertParams(T object);

    /**
     * {@link SimpleJDBCWrapper#insertAndReturnWithKeys} または {@link SimpleJDBCWrapper#insertOneAndReturnWithKey} の実行時に
     * データベースの自動採番で得られたキーと INSERT 元のオブジェクトを統合して、キーを持ったオブジェクトを生成する処理です。
     */
    T zipWithKey(long key, T objectWithoutKey);

}
