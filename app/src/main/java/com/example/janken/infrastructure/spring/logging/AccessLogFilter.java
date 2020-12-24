package com.example.janken.infrastructure.spring.logging;


import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
class AccessLogFilter implements Filter {

    private static final String REQUEST_ID_KEY = "REQUEST_ID";

    private static final String LOG_FORMAT_FOR_ACCESS_REQUEST = "[Request] method={}, url={}";
    private static final String LOG_FORMAT_FOR_ACCESS_RESPONSE = "[Response] method={}, url={}, status={}";

    /**
     * コントローラの処理の前後でアクセスログを出力します。
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        val uuid = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID_KEY, uuid);

        val attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        val httpServletRequest = attributes.getRequest();
        val method = httpServletRequest.getMethod();
        val urlWithQueryString = urlWithQueryString(httpServletRequest);

        log.info(LOG_FORMAT_FOR_ACCESS_REQUEST, method, urlWithQueryString);

        try {
            chain.doFilter(request, response);

        } finally {
            val status = Optional.of(attributes)
                    .map(ServletRequestAttributes::getResponse)
                    .map(HttpServletResponse::getStatus)
                    .orElse(null);

            log.info(LOG_FORMAT_FOR_ACCESS_RESPONSE, method, urlWithQueryString, status);

            MDC.remove(REQUEST_ID_KEY);
        }
    }

    /**
     * URL をクエリストリングとともに返します。
     * <p>
     * クエリストリングが空の場合、URL のみを返します。
     */
    private String urlWithQueryString(HttpServletRequest request) {
        val uri = request.getRequestURI();
        val queryString = request.getQueryString();
        return queryString == null ? uri : uri + "?" + queryString;
    }

}
