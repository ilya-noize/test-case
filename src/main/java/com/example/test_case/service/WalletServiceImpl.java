package com.example.test_case.service;

import com.example.test_case.dto.WalletDto;
import com.example.test_case.dto.WalletNewDto;
import com.example.test_case.dto.WalletOperationDto;
import com.example.test_case.exception.WalletBalancePaymentsException;
import com.example.test_case.exception.WalletNotFoundException;
import com.example.test_case.exception.WalletOperationException;
import com.example.test_case.mapper.WalletMapper;
import com.example.test_case.model.OperationType;
import com.example.test_case.model.Wallet;
import com.example.test_case.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.UUID;

import static com.example.test_case.model.OperationType.DEPOSIT;
import static java.lang.String.format;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Override
    public WalletDto create(WalletNewDto dto) {
        if (dto == null)
            throw new WalletOperationException("Invalid data for executing the request");
        int balance = dto.getBalance();
        if (balance < 0)
            throw new WalletBalancePaymentsException("Negative balance for wallet");
        Wallet wallet = walletRepository.save(
                Wallet.builder().balance(balance).build());

        log.debug("Wallet created:{}", wallet.getId());

        return walletMapper.toDto(wallet);
    }

    @Override
    public WalletDto update(WalletOperationDto walletOperationDto) {
        UUID uuid = walletOperationDto.getWalletId();
        Wallet walletOrigin = getExistsWalletById(uuid);

        String operationType = walletOperationDto.getOperationType();
        int balance = walletOrigin.getBalance();
        int amount = walletOperationDto.getAmount();

        if (amount <= 0) {
            throw new WalletBalancePaymentsException(format("Wrong amount: %d", amount));
        }

        balance = getBalanceAfterOperationType(operationType, balance, amount);

        Wallet wallet = Wallet.builder().id(uuid).balance(balance).build();

        log.debug("Wallet updated:{}", wallet.getId());

        return walletMapper.toDto(
                walletRepository.save(wallet)
        );
    }

    @Override
    public WalletDto get(UUID uuid) {
        Wallet existsWalletById = getExistsWalletById(uuid);
        log.debug("Wallet received:{}", uuid);

        return walletMapper.toDto(existsWalletById);
    }

    @Override
    public void delete(UUID uuid) {
        Wallet existsWalletById = getExistsWalletById(uuid);
        walletRepository.delete(existsWalletById);
        log.debug("Wallet deleted:{}", uuid);
    }

    private int getBalanceAfterOperationType(
            String operationType,
            int balance,
            int amount
    ) {
        try {
            OperationType operation = EnumUtils.findEnumInsensitiveCase(
                    OperationType.class,
                    operationType
            );
            log.debug("The operation is fixed: {}", operation);

            return operation == DEPOSIT ? balance + amount : getWithdraw(balance, amount);
        } catch (IllegalArgumentException e) {
            throw new WalletOperationException(format("Invalid operation type: %s", operationType));
        }
    }

    private int getWithdraw(int balance, int amount) {
        balance -= amount;
        if (balance < 0) {
            throw new WalletBalancePaymentsException(
                    format("Insufficient funds. %d is to much.", amount)
            );
        }

        return balance;
    }

    private Wallet getExistsWalletById(UUID uuid) {

        return walletRepository.findById(uuid)
                .orElseThrow(() -> new WalletNotFoundException(format("Wallet not found: %s", uuid)));
    }
}
