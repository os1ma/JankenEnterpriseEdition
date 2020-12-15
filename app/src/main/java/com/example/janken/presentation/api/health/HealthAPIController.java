package com.example.janken.presentation.api.health;

import com.example.janken.domain.transaction.TransactionManager;
import com.example.janken.infrastructure.jdbctransaction.JDBCTransactionManager;
import com.example.janken.infrastructure.jdbctransaction.SimpleJDBCWrapper;
import com.example.janken.infrastructure.jdbctransaction.SingleRowMapper;
import com.example.janken.registry.ServiceLocator;
import com.google.gson.Gson;
import lombok.val;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/health")
public class HealthAPIController extends HttpServlet {

    private TransactionManager tm = ServiceLocator.resolve(TransactionManager.class);
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
