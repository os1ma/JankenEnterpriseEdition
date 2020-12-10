package com.example.janken.infrastructure.jdbctransaction;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {

    /**
     * {@link SimpleJDBCWrapper#findList} または {@link SimpleJDBCWrapper#findFirst} の実行時に
     * 結果の ResultSet をオブジェクトにマッピングする処理です。
     */
    T map(ResultSet rs) throws SQLException;

}
