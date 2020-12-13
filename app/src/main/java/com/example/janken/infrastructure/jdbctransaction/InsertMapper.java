package com.example.janken.infrastructure.jdbctransaction;

import java.util.Map;

public interface InsertMapper<T> {

    /**
     * {@link SimpleJDBCWrapper#insertAll} または {@link SimpleJDBCWrapper#insertOne} の実行時に
     * INSERT のパラメータをオブジェクトから設定するため、カラム名・値のマップを生成する処理です。
     */
    Map<String, Object> object2InsertParams(T object);

}
