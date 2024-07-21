package com.example.test_case.exception;

public class WalletBalancePaymentsException extends  RuntimeException {
    public WalletBalancePaymentsException(String message) {
        super(message);
    }
}
