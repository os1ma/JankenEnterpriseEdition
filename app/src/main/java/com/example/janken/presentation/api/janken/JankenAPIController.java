package com.example.janken.presentation.api.janken;

import com.example.janken.application.service.JankenApplicationService;
import com.example.janken.registry.ServiceLocator;
import com.google.gson.Gson;
import lombok.val;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/v1/jankens")
public class JankenAPIController extends HttpServlet {

    private JankenApplicationService service = ServiceLocator.resolve(JankenApplicationService.class);

    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        // リクエストボディの読み込み
        val reader = request.getReader();
        val sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String requestBodyStr = sb.toString();
        val requestBody = gson.fromJson(requestBodyStr, JankenPostRequestBody.class);

        // ユースケースを実行
        val maybeWinner = service.play(
                requestBody.getPlayer1Id(),
                requestBody.player1Hand(),
                requestBody.getPlayer2Id(),
                requestBody.player2Hand());

        // レスポンスヘッダを設定
        response.setContentType("application/json");

        // レスポンスボディを設定
        val responseBody = JankenPostResponseBody.of(maybeWinner);
        val responseBodyStr = gson.toJson(responseBody);
        val writer = response.getWriter();
        writer.print(responseBodyStr);
        writer.flush();
    }

}
