package com.example.test_case.service;

import com.example.test_case.dto.WalletDto;
import com.example.test_case.dto.WalletNewDto;
import com.example.test_case.dto.WalletOperationDto;
import com.example.test_case.exception.WalletBalancePaymentsException;
import com.example.test_case.exception.WalletNotFoundException;
import com.example.test_case.exception.WalletOperationException;
import com.example.test_case.mapper.WalletMapper;
import com.example.test_case.model.Wallet;
import com.example.test_case.repository.WalletRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class WalletServiceIntegrationTest {
    private static final int MAX_AMOUNT = 1000;
    private static final EasyRandom RANDOM = new EasyRandom();
    private static final UUID RANDOM_UUID = UUID.randomUUID();
    private static final int MAX_BALANCE = RANDOM.nextInt(2, 100);
    private final int balance = RANDOM.nextInt(1, MAX_BALANCE);

    @InjectMocks
    private WalletServiceImpl service;
    @Mock
    private WalletRepository repository;
    @Mock
    private WalletMapper mapper;

    private Wallet expectedWallet;
    private int amount;
    private WalletOperationDto walletOperationDto;

    @BeforeEach
    void setUp() {
        expectedWallet = Wallet.builder()
                .id(RANDOM_UUID)
                .balance(balance)
                .build();
        amount = RANDOM.nextInt(MAX_BALANCE, MAX_AMOUNT);
    }


    @Test
    @DisplayName("CREATE: When normal balance wallet Then OK")
    void create_whenNormalBalance_thenOk() {
        Wallet wallet = RANDOM.nextObject(Wallet.class);
        wallet.setBalance(balance);
        when(repository.save(any(Wallet.class))).thenReturn(wallet);

        WalletDto expected = WalletDto.builder()
                .walletId(wallet.getId())
                .balance(wallet.getBalance()).build();
        when(mapper.toDto(wallet)).thenReturn(expected);

        WalletNewDto walletNewDto = WalletNewDto.builder()
                .balance(wallet.getBalance()).build();
        WalletDto walletDto = service.create(walletNewDto);

        assertEquals(expected, walletDto);

        verify(repository, times(1)).save(any(Wallet.class));
    }

    @Test
    @DisplayName("CREATE: When negative balance wallet Then throw Exception")
    void create_whenNegativeBalance_thenThrowException() {
        WalletNewDto walletNewDto = new WalletNewDto(-1);

        assertThrows(WalletBalancePaymentsException.class,
                () -> service.create(walletNewDto),
                "Negative balance for wallet");

        verify(repository, never()).save(any(Wallet.class));
    }

    @Test
    @DisplayName("UPDATE: When update not exists wallet Then NotFoundException")
    void update_whenNotExistsWallet_thenThrowException() {
        when(repository.findById(RANDOM_UUID))
                .thenReturn(Optional.empty());

        walletOperationDto = WalletOperationDto.builder()
                .walletId(RANDOM_UUID)
                .operationType("DEPOSIT")
                .amount(amount).build();

        assertThrows(WalletNotFoundException.class,
                () -> service.update(walletOperationDto),
                "Wallet not found");

        verify(repository, times(1)).findById(RANDOM_UUID);
        verify(repository, never()).save(any(Wallet.class));
    }

    @Test
    @DisplayName("UPDATE: When update wallet deposit Then change wallet balance")
    void update_whenWalletWrongOperation_thenThrowException() {
        Wallet expectedWallet = Wallet.builder()
                .id(RANDOM_UUID)
                .balance(balance)
                .build();
        when(repository.findById(RANDOM_UUID))
                .thenReturn(Optional.ofNullable(expectedWallet));

        int amount = RANDOM.nextInt(1, MAX_AMOUNT);
        String operationType = RANDOM.toString();
        WalletOperationDto walletOperationDto = WalletOperationDto.builder()
                .walletId(RANDOM_UUID)
                .operationType(operationType)
                .amount(amount).build();

        assertThrows(WalletOperationException.class,
                () -> service.update(walletOperationDto),
                "No enum constant OperationType." + operationType);

        verify(repository, times(1)).findById(RANDOM_UUID);
        verify(repository, never()).save(any(Wallet.class));
    }

    @Test
    @DisplayName("UPDATE: When negative amount - then throw exception")
    void update_whenNegativeAmount_thenThrowException() {
        Wallet expectedWallet = Wallet.builder()
                .id(RANDOM_UUID)
                .balance(balance)
                .build();
        when(repository.findById(RANDOM_UUID))
                .thenReturn(Optional.ofNullable(expectedWallet));

        int amount = RANDOM.nextInt(Integer.MIN_VALUE, 0);
        String operationType = "WITHDRAW";
        WalletOperationDto walletOperationDto = WalletOperationDto.builder()
                .walletId(RANDOM_UUID)
                .operationType(operationType)
                .amount(amount).build();

        assertThrows(WalletBalancePaymentsException.class,
                () -> service.update(walletOperationDto),
                "Wrong amount: " + amount);

        verify(repository, times(1)).findById(RANDOM_UUID);
        verify(repository, never()).save(any(Wallet.class));
    }

    @Test
    @DisplayName("UPDATE: When Insufficient funds for withdraw - then throw exception")
    void update_whenInsufficientFundsForWithdraw_thenThrowException() {
        when(repository.findById(RANDOM_UUID))
                .thenReturn(Optional.ofNullable(expectedWallet));

        int amount = RANDOM.nextInt(MAX_BALANCE, MAX_AMOUNT);
        String operationType = "WITHDRAW";
        WalletOperationDto walletOperationDto = WalletOperationDto.builder()
                .walletId(RANDOM_UUID)
                .operationType(operationType)
                .amount(amount).build();

        assertThrows(WalletBalancePaymentsException.class,
                () -> service.update(walletOperationDto),
                format("Insufficient funds. %d is to much.", amount)
        );

        verify(repository, times(1)).findById(RANDOM_UUID);
        verify(repository, never()).save(any(Wallet.class));
    }

    @Test
    @DisplayName("GET: When exists wallet Then Return Wallet by UUID")
    void get_whenWalletExists_ThenReturnWalletByUuid() {
        WalletDto expectedDto = WalletDto.builder()
                .walletId(RANDOM_UUID)
                .balance(balance)
                .build();

        when(mapper.toDto(expectedWallet))
                .thenReturn(expectedDto);
        when(repository.findById(RANDOM_UUID))
                .thenReturn(Optional.ofNullable(expectedWallet));

        WalletDto walletDto = service.get(RANDOM_UUID);
        WalletDto expected = new WalletDto(RANDOM_UUID, balance);
        assertEquals(expected, walletDto);

        verify(repository, times(1)).findById(RANDOM_UUID);
    }

    @Test
    @DisplayName("GET: When not exists wallet Then NotFoundException")
    void get_whenNotExistsWallet_thenThrowException() {

        when(repository.findById(RANDOM_UUID))
                .thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class,
                () -> service.get(RANDOM_UUID),
                "Wallet not found");

        verify(repository, times(1)).findById(RANDOM_UUID);
    }

    @Test
    @DisplayName("DELETE: When not exists wallet Then throw Exception")
    void delete_whenNotExists_thenThrowException() {
        when(repository.findById(RANDOM_UUID))
                .thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class,
                () -> service.delete(RANDOM_UUID),
                "Wallet not found");

        verify(repository, times(1)).findById(RANDOM_UUID);
        verify(repository, never()).delete(any(Wallet.class));
    }

    @Test
    @DisplayName("DELETE: When not exists wallet Then throw Exception")
    void delete_whenExists_thenDeleteWallet() {
        when(repository.findById(RANDOM_UUID))
                .thenReturn(Optional.ofNullable(expectedWallet));

        service.delete(RANDOM_UUID);

        verify(repository, times(1)).findById(RANDOM_UUID);
        verify(repository, times(1)).delete(expectedWallet);

    }
}
