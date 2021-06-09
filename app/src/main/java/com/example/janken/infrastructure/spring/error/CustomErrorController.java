package com.example.janken.infrastructure.spring.error;

import com.example.janken.application.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@RestController
@Slf4j
public class CustomErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    private static final List<Class<? extends Throwable>> BAD_REQUEST_EXCEPTIONS = List.of(
            MethodArgumentNotValidException.class,
            BindException.class,
            MissingPathVariableException.class,
            HttpMessageConversionException.class);

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<ErrorResponseBody> error(WebRequest request) {
        val error = errorAttributes.getError(request);

        if (error == null) {
            // エラーが存在しない場合は 404

            val status = HttpStatus.NOT_FOUND;
            val body = new ErrorResponseBody(status.value(), "Not Found");
            return ResponseEntity.status(status).body(body);

        } else if (BAD_REQUEST_EXCEPTIONS.contains(error.getClass())) {
            // Spring がリクエストを処理する際のエラーの場合

            val status = HttpStatus.BAD_REQUEST;
            val body = new ErrorResponseBody(status.value(), "Invalid request format");
            return ResponseEntity.status(status).body(body);

        } else if (error instanceof ApplicationException) {
            // アプリケーション上で明示的に投げたエラーの場合

            val applicationException = (ApplicationException) error;
            val status = applicationException.getStatus();

            if (status.is5xxServerError()) {
                log.error(error.getMessage(), error);
            } else {
                log.warn(error.getMessage(), error);
            }

            val body = new ErrorResponseBody(status.value(), applicationException.getMessage());
            return ResponseEntity.status(status).body(body);

        } else {
            // 想定外のエラーの場合

            log.error(error.getMessage(), error);

            val status = HttpStatus.INTERNAL_SERVER_ERROR;
            val body = new ErrorResponseBody(status.value(), "Unexpected error occurred");
            return ResponseEntity.status(status).body(body);
        }
    }

}
