package com.example.janken.infrastructure.spring.error;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class ErrorResponseBody {
    private int status;
    private String message;
}
