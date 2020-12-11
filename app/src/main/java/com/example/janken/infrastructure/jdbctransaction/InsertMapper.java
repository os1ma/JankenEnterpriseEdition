package com.example.janken.infrastructure.jdbctransaction;

import java.util.Map;

public interface InsertMapper<T> {

    /**
     * {@link SimpleJDBCWrapper#insertAndReturnObjectWithKeys} または {@link SimpleJDBCWrapper#insertOneAndReturnObjectWithKey} の実行時に
     * INSERT のパラメータをオブジェクトから設定するため、カラム名・値のマップを生成する処理です。
     */
    Map<String, Object> object2InsertParams(T object);

    /**
     * {@link SimpleJDBCWrapper#insertAndReturnObjectWithKeys} または {@link SimpleJDBCWrapper#insertOneAndReturnObjectWithKey} の実行時に
     * データベースの自動採番で得られたキーと INSERT 元のオブジェクトを統合して、キーを持ったオブジェクトを生成する処理です。
     */
    T zipWithKey(long key, T objectWithoutKey);

}
