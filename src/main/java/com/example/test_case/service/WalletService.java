package com.example.test_case.service;

import com.example.test_case.dto.WalletDto;
import com.example.test_case.dto.WalletNewDto;
import com.example.test_case.dto.WalletOperationDto;

import java.util.UUID;

public interface WalletService {

    WalletDto create(WalletNewDto walletDto);

    WalletDto update(WalletOperationDto walletOperationDto);

    WalletDto get(UUID uuid);

    void delete(UUID uuid);
}
