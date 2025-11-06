package com.eazybank.account.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ErrorResponse {

    private String apiPath;
    private HttpStatus errorCode;

    private String errorMsg;

    private LocalDateTime errorTime;
}
