package com.example.test_case.exception;

import com.example.test_case.exception.model.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
    private void logError(HttpStatus status, String error, ZonedDateTime dateTime, Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTraceString = sw.toString().replace(", ", "\n");

        log.error("[!] Received the status {}. Error: {}. Time: {}.\n{}", status, error, dateTime, stackTraceString);
    }

    @ExceptionHandler(WalletNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorMessage handleWalletNotFoundException(
            WalletNotFoundException walletNotFoundException
    ) {
        String message = walletNotFoundException.getMessage();
        ZonedDateTime now = ZonedDateTime.now();
        logError(NOT_FOUND, message, now, walletNotFoundException);

        return new ErrorMessage(NOT_FOUND.value(), message, now);
    }

    @ExceptionHandler(WalletBalancePaymentsException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleWalletBalancePaymentsException(
            WalletBalancePaymentsException walletBalancePaymentsException
    ) {
        String message = walletBalancePaymentsException.getMessage();
        ZonedDateTime now = ZonedDateTime.now();
        logError(BAD_REQUEST, message, now, walletBalancePaymentsException);

        return new ErrorMessage(BAD_REQUEST.value(), message, now);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleIllegalArgumentException(
            IllegalArgumentException illegalArgumentException
    ) {
        String message = illegalArgumentException.getMessage();
        ZonedDateTime now = ZonedDateTime.now();
        logError(BAD_REQUEST, message, now, illegalArgumentException);

        return new ErrorMessage(BAD_REQUEST.value(), message, now);
    }
}
