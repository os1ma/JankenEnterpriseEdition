package com.example.janken.infrastructure.spring;

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
