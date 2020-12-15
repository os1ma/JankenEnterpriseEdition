package com.example.janken.presentation.api;

import com.google.gson.Gson;
import lombok.val;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class APIControllerUtils {

    private static Gson GSON = new Gson();

    public static <T> T getRequestBody(HttpServletRequest request, Class<T> clazz) {
        try {
            val reader = request.getReader();

            val sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBodyStr = sb.toString();

            return GSON.fromJson(requestBodyStr, clazz);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setResponseBody(HttpServletResponse response, Object object) {
        try {
            response.setContentType("application/json");

            val responseBodyStr = GSON.toJson(object);

            val writer = response.getWriter();
            writer.print(responseBodyStr);
            writer.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
