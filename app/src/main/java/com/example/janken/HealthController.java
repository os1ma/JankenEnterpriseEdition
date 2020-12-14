package com.example.janken;

import com.example.janken.domain.transaction.TransactionManager;
import com.example.janken.infrastructure.jdbctransaction.JDBCTransactionManager;
import com.example.janken.infrastructure.jdbctransaction.SimpleJDBCWrapper;
import com.example.janken.infrastructure.jdbctransaction.SingleRowMapper;
import com.google.gson.Gson;
import lombok.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/health")
public class HealthController extends HttpServlet {

    private TransactionManager tm = new JDBCTransactionManager();
    private SimpleJDBCWrapper simpleJDBCWrapper = new SimpleJDBCWrapper();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        // DB との疎通チェック
        tm.transactional(tx -> {
            simpleJDBCWrapper.findFirst(tx, new SingleRowMapper<Long>(), "SELECT 1");
        });

        // レスポンスヘッダを設定
        response.setContentType("application/json");

        // レスポンスボディを設定
        val status = 200;
        val responseBody = new HealthResponseBody(status);
        val responseBodyStr = gson.toJson(responseBody);
        val writer = response.getWriter();
        writer.print(responseBodyStr);
        writer.flush();
    }

}

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
class HealthResponseBody {
    private int status;
}
