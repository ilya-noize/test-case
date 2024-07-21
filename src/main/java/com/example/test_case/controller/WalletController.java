package com.example.test_case.controller;

import com.example.test_case.dto.WalletDto;
import com.example.test_case.dto.WalletNewDto;
import com.example.test_case.dto.WalletOperationDto;
import com.example.test_case.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static java.lang.String.format;

/**
 * Должны быть написаны миграции для базы данных с помощью liquibase
 * Обратите особое внимание проблемам при работе в конкурентной среде (1000 RPS по
 * одному кошельку).
 * <p>
 * Ни один запрос не должен быть не обработан (50Х error)
 * <p>
 * Предусмотрите соблюдение формата ответа для заведомо неверных запросов, когда
 * кошелька не существует, не валидный json, или недостаточно средств.
 * <p>
 * приложение должно запускаться в докер контейнере, база данных тоже, вся система
 * должна подниматься с помощью docker-compose
 * предусмотрите возможность настраивать различные параметры как на стороне
 * приложения так и базы данных без пересборки контейнеров.
 * эндпоинты должны быть покрыты тестами.
 * Решенное задание залить на гитхаб, предоставить ссылку
 * Все возникающие вопросы по заданию решать самостоятельно, по своему
 * усмотрению.
 */

@RestController
@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;


    @PostMapping("/wallet")
    @ResponseStatus(HttpStatus.CREATED)
    public WalletDto create(@RequestBody @Valid WalletNewDto dto) {
        log.info("POST /api/v1/wallet BALANCE:{}.", dto.getBalance());

        return walletService.create(dto);
    }

    @PutMapping("/wallet")
    public WalletDto update(@RequestBody WalletOperationDto dto) {
        log.info("PUT /api/v1/wallet WID:{}, OPER_TYPE{}, AMOUNT{}.",
                dto.getWalletId(),
                dto.getOperationType(),
                dto.getAmount());

        return walletService.update(dto);
    }

    @GetMapping("/wallets/{id}")
    public WalletDto get(@PathVariable UUID id) {
        log.info(format("GET /api/v1/wallets/%s", id.toString()));

        return walletService.get(id);
    }

    @DeleteMapping("/wallets/{id}")
    public void delete(@PathVariable UUID id) {
        log.info(format("DELETE /api/v1/wallets/%s", id.toString()));

        walletService.delete(id);
    }
}