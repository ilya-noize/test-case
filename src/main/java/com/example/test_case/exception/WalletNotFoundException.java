package com.example.test_case.exception;

public class WalletNotFoundException extends  RuntimeException {
    public WalletNotFoundException(String message) {
        super(message);
    }
}
