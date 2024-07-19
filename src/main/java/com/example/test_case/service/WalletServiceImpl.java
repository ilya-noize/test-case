package com.example.test_case.service;

import com.example.test_case.dto.WalletDto;
import com.example.test_case.dto.WalletOperationDto;
import com.example.test_case.exception.WalletException;
import com.example.test_case.mapper.WalletMapper;
import com.example.test_case.model.OperationType;
import com.example.test_case.model.Wallet;
import com.example.test_case.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.example.test_case.model.OperationType.DEPOSIT;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Override
    public WalletDto create(WalletDto walletDto) {
        Wallet wallet = walletMapper.toEntityFromDto(walletDto);

        return walletMapper.toDto(
                walletRepository.save(wallet)
        );
    }

    @Override
    public WalletDto update(WalletOperationDto walletOperationDto) {
        UUID uuid = walletOperationDto.getWalletId();
        Wallet walletOrigin = getExistsWalletById(uuid);

        OperationType operationType = walletOperationDto.getOperationType();
        int balance = walletOrigin.getBalance();
        int amount = walletOperationDto.getAmount();

        balance = getBalanceAfterOperationType(operationType, balance, amount);

        Wallet wallet = Wallet.builder().id(uuid).balance(balance).build();

        return walletMapper.toDto(
                walletRepository.save(wallet)
        );
    }

    @Override
    public WalletDto get(UUID uuid) {

        return walletMapper.toDto(getExistsWalletById(uuid));
    }

    @Override
    public void delete(UUID uuid) {
        try {
            walletRepository.deleteById(uuid);
        } catch (WalletException e) {
            throw new WalletException(e.getMessage());
        }
    }

    private int getBalanceAfterOperationType(OperationType operationType, int balance, int amount) {

        return operationType == DEPOSIT ? balance + amount : getWithdraw(balance, amount);
    }

    private int getWithdraw(int balance, int amount) {
        balance -= amount;
        if (balance < 0) {
            throw new WalletException("Insufficient funds");
        }

        return balance;
    }

    private Wallet getExistsWalletById(UUID uuid) {

        return walletRepository.findById(uuid)
                .orElseThrow(() -> new WalletException("Wallet not found"));
    }
}
