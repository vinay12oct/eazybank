package com.eazybank.account.exception;

public class MobileAlreadyExistsException extends RuntimeException {
    public MobileAlreadyExistsException(String message) {
        super(message);
    }
}
