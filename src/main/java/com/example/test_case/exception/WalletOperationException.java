package com.example.test_case.exception;

public class WalletOperationException extends RuntimeException {
    public WalletOperationException(String message) {
        super(message);
    }
}
