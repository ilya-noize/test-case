package com.example.test_case.exception;

import com.example.test_case.exception.model.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
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

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorMessage handleThrowable(
            Throwable throwable
    ) {
        String message = throwable.getMessage().split(":")[0];
        ZonedDateTime now = ZonedDateTime.now();
        logError(INTERNAL_SERVER_ERROR, message, now, throwable);

        return new ErrorMessage(INTERNAL_SERVER_ERROR.value(), message, now);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(NOT_ACCEPTABLE)
    public ErrorMessage handleHttpMediaTypeNotAcceptableException(
            HttpMediaTypeNotAcceptableException httpMediaTypeNotAcceptableException
    ) {
        String message = httpMediaTypeNotAcceptableException.getMessage().split(":")[0];
        ZonedDateTime now = ZonedDateTime.now();
        logError(NOT_ACCEPTABLE, message, now, httpMediaTypeNotAcceptableException);

        return new ErrorMessage(NOT_ACCEPTABLE.value(), message, now);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleMethodArgumentNotValidException(
            MethodArgumentNotValidException methodArgumentNotValidException
    ) {
        BindingResult result = methodArgumentNotValidException.getBindingResult();

        String message = result.getAllErrors().get(0).getDefaultMessage();
        ZonedDateTime now = ZonedDateTime.now();
        logError(BAD_REQUEST, message, now, methodArgumentNotValidException);

        return new ErrorMessage(BAD_REQUEST.value(), message, now);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(NOT_ACCEPTABLE)
    public ErrorMessage handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException
    ) {
        String message = httpRequestMethodNotSupportedException.getMessage().split(":")[0];
        ZonedDateTime now = ZonedDateTime.now();
        logError(NOT_ACCEPTABLE, message, now, httpRequestMethodNotSupportedException);

        return new ErrorMessage(NOT_ACCEPTABLE.value(), message, now);
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleNullPointerException(
            NullPointerException nullPointerException
    ) {
        String message = nullPointerException.getMessage().split(":")[0];
        ZonedDateTime now = ZonedDateTime.now();
        logError(BAD_REQUEST, message, now, nullPointerException);

        return new ErrorMessage(BAD_REQUEST.value(), message, now);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleNoResourceFoundException(
            NoResourceFoundException noResourceFoundException
    ) {
        String message = noResourceFoundException.getMessage().split(":")[0];
        ZonedDateTime now = ZonedDateTime.now();
        logError(BAD_REQUEST, message, now, noResourceFoundException);

        return new ErrorMessage(BAD_REQUEST.value(), message, now);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleHttpMessageNotReadableException(
            HttpMessageNotReadableException httpMessageNotReadableException
    ) {
        String message = httpMessageNotReadableException.getMessage().split(":")[0];
        ZonedDateTime now = ZonedDateTime.now();
        logError(BAD_REQUEST, message, now, httpMessageNotReadableException);

        return new ErrorMessage(BAD_REQUEST.value(), message, now);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleIllegalArgumentException(
            IllegalArgumentException illegalArgumentException
    ) {
        String message = illegalArgumentException.getMessage().split(":")[0];
        ZonedDateTime now = ZonedDateTime.now();
        logError(BAD_REQUEST, message, now, illegalArgumentException);

        return new ErrorMessage(BAD_REQUEST.value(), message, now);
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
        String message = walletBalancePaymentsException.getMessage().split(":")[0];
        ZonedDateTime now = ZonedDateTime.now();
        logError(BAD_REQUEST, message, now, walletBalancePaymentsException);

        return new ErrorMessage(BAD_REQUEST.value(), message, now);
    }

    @ExceptionHandler(WalletOperationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleWalletOperationException(
            WalletOperationException walletOperationException
    ) {
        String message = walletOperationException.getMessage();
        ZonedDateTime now = ZonedDateTime.now();
        logError(BAD_REQUEST, message, now, walletOperationException);

        return new ErrorMessage(BAD_REQUEST.value(), message, now);
    }
}
