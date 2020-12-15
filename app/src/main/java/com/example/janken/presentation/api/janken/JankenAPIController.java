package com.example.janken.presentation.api.janken;

import com.example.janken.application.service.JankenApplicationService;
import com.example.janken.presentation.api.APIControllerUtils;
import com.example.janken.registry.ServiceLocator;
import lombok.val;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/jankens")
public class JankenAPIController extends HttpServlet {

    private JankenApplicationService service = ServiceLocator.resolve(JankenApplicationService.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        val requestBody = APIControllerUtils.getRequestBody(request, JankenPostRequestBody.class);

        val maybeWinner = service.play(
                requestBody.getPlayer1Id(),
                requestBody.player1Hand(),
                requestBody.getPlayer2Id(),
                requestBody.player2Hand());

        val responseBody = JankenPostResponseBody.of(maybeWinner);
        APIControllerUtils.setResponseBody(response, responseBody);
    }

}
