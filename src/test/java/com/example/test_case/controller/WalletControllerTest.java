package com.example.test_case.controller;

import com.example.test_case.dto.WalletDto;
import com.example.test_case.dto.WalletNewDto;
import com.example.test_case.dto.WalletOperationDto;
import com.example.test_case.exception.WalletBalancePaymentsException;
import com.example.test_case.exception.WalletNotFoundException;
import com.example.test_case.exception.WalletOperationException;
import com.example.test_case.exception.model.ErrorMessage;
import com.example.test_case.service.WalletServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.ZonedDateTime;
import java.util.UUID;

import static com.example.test_case.model.OperationType.DEPOSIT;
import static com.example.test_case.model.OperationType.WITHDRAW;
import static java.lang.String.format;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WalletController.class)
@AutoConfigureWebMvc
@AutoConfigureMockMvc
class WalletControllerTest {
    public static final EasyRandom RANDOM = new EasyRandom();
    public static final String REQUIRED_REQUEST_BODY_IS_MISSING = "Required request body is missing";
    private final String POST_PATH = "/api/v1/wallet";
    private final String PUT_PATH = POST_PATH;
    private final String GET_PATH = "/api/v1/wallets/{id}";
    private final String DELETE_PATH = GET_PATH;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private WalletServiceImpl walletService;
    private WalletNewDto walletNewDto;
    private WalletDto walletDto;
    private WalletOperationDto walletOperationDto;
    private UUID WALLET_UUID;
    private Integer amount;

    @BeforeEach
    void setUp() {
        WALLET_UUID = UUID.randomUUID();
        amount = RANDOM.nextInt(1, 100);
        walletNewDto = new WalletNewDto(1000);
        walletOperationDto = WalletOperationDto.builder()
                .walletId(WALLET_UUID)
                .operationType("DEPOSIT")
                .amount(amount).build();
        walletDto = new WalletDto(WALLET_UUID, 1000);
    }

    @Test
    @DisplayName("POST /api/v1/wallet 200:OK")
    void create() throws Exception {

        when(walletService.create(walletNewDto))
                .thenReturn(walletDto);

        RequestBuilder requestBuilder = post(POST_PATH)
                .content(mapper.writeValueAsString(walletNewDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ResultMatcher[] resultMatchers = {
                jsonPath("$.walletId").value(walletDto.getWalletId().toString()),
                jsonPath("$.balance").value(walletDto.getBalance())
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/v1/wallet 400:BAD_REQUEST - invalid json")
    void create_whenInvalidJson_then400() throws Exception {
        walletNewDto.setBalance(null);
        String error = "Invalid data for executing the request";
        when(walletService.create(walletNewDto))
                .thenThrow(new WalletOperationException(error));

        RequestBuilder requestBuilder = post(POST_PATH)
                .content(mapper.writeValueAsString(walletNewDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ErrorMessage errorMessage = new ErrorMessage(BAD_REQUEST.value(), error, ZonedDateTime.now());
        ResultMatcher[] resultMatchers = {
                jsonPath("$.status").value(errorMessage.status()),
                jsonPath("$.error").value(errorMessage.error()),
                jsonPath("$.dateTime").exists()
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("POST /api/v1/wallet 400:BAD_REQUEST - null json")
    void create_whenNullJson_then400() throws Exception {
        walletNewDto = null;
        String error = REQUIRED_REQUEST_BODY_IS_MISSING;
        when(walletService.create(null))
                .thenThrow(new WalletOperationException(error));

        RequestBuilder requestBuilder = post(POST_PATH)
                .content(mapper.writeValueAsString(walletNewDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ErrorMessage errorMessage = new ErrorMessage(BAD_REQUEST.value(), error, ZonedDateTime.now());
        ResultMatcher[] resultMatchers = {
                jsonPath("$.status").value(errorMessage.status()),
                jsonPath("$.error").value(errorMessage.error()),
                jsonPath("$.dateTime").exists()
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/wallet 400:BAD_REQUEST - Negative balance")
    void create_whenNegativeBalance_thenException_Status400() throws Exception {
        walletNewDto.setBalance(-1);
        String error = "Negative balance for wallet";

        when(walletService.create(walletNewDto))
                .thenThrow(new WalletBalancePaymentsException(error));

        RequestBuilder requestBuilder = post(POST_PATH)
                .content(mapper.writeValueAsString(walletNewDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ErrorMessage errorMessage = new ErrorMessage(BAD_REQUEST.value(), error, ZonedDateTime.now());
        ResultMatcher[] resultMatchers = {
                jsonPath("$.status").value(errorMessage.status()),
                jsonPath("$.error").value(errorMessage.error()),
                jsonPath("$.dateTime").exists()
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/wallet 400:BAD_REQUEST - unknown operation")
    void update_whenUnknownOperation_thenBadRequest400() throws Exception {
        String unknownOperation = RANDOM.toString();
        walletOperationDto.setOperationType(unknownOperation);
        String error = format("No enum constant OperationType.%s", unknownOperation);

        when(walletService.update(walletOperationDto))
                .thenThrow(new IllegalArgumentException(error));

        RequestBuilder requestBuilder = put(PUT_PATH)
                .content(mapper.writeValueAsString(walletOperationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ErrorMessage errorMessage = new ErrorMessage(BAD_REQUEST.value(), error, ZonedDateTime.now());

        ResultMatcher[] resultMatchers = {
                jsonPath("$.status").value(errorMessage.status()),
                jsonPath("$.error").value(errorMessage.error()),
                jsonPath("$.dateTime").exists()
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/wallet 404:NOT_FOUND - not found wallet")
    void update_whenNotFount_then404status() throws Exception {
        String error = "Wallet not found";

        when(walletService.update(walletOperationDto))
                .thenThrow(new WalletNotFoundException(error));

        RequestBuilder requestBuilder = put(PUT_PATH)
                .content(mapper.writeValueAsString(walletOperationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ErrorMessage errorMessage = new ErrorMessage(NOT_FOUND.value(), error, ZonedDateTime.now());

        ResultMatcher[] resultMatchers = {
                jsonPath("$.status").value(errorMessage.status()),
                jsonPath("$.error").value(errorMessage.error()),
                jsonPath("$.dateTime").exists()
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/wallet 400:BAD_REQUEST - negative amount.")
    void update_whenNegativeAmount_then400status() throws Exception {
        walletOperationDto.setOperationType(DEPOSIT.name());
        String error = "Wrong amount";

        when(walletService.update(walletOperationDto))
                .thenThrow(new WalletBalancePaymentsException(error));

        RequestBuilder requestBuilder = put(PUT_PATH)
                .content(mapper.writeValueAsString(walletOperationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ErrorMessage errorMessage = new ErrorMessage(BAD_REQUEST.value(), error, ZonedDateTime.now());

        ResultMatcher[] resultMatchers = {
                jsonPath("$.status").value(errorMessage.status()),
                jsonPath("$.error").value(errorMessage.error()),
                jsonPath("$.dateTime").exists()
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/wallet 400:BAD_REQUEST - null json")
    void update_whenNullJson_then400() throws Exception {
        walletOperationDto = null;
        String error = REQUIRED_REQUEST_BODY_IS_MISSING;
        when(walletService.update(walletOperationDto))
                .thenThrow(new WalletOperationException(error));

        RequestBuilder requestBuilder = put(POST_PATH)
                .content(mapper.writeValueAsString(walletOperationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ErrorMessage errorMessage = new ErrorMessage(BAD_REQUEST.value(), error, ZonedDateTime.now());
        ResultMatcher[] resultMatchers = {
                jsonPath("$.status").value(errorMessage.status()),
                jsonPath("$.error").value(errorMessage.error()),
                jsonPath("$.dateTime").exists()
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("PUT /api/v1/wallet 400:BAD_REQUEST - null walletId json")
    void update_whenNullWalletIdJson_then400() throws Exception {
        walletOperationDto.setWalletId(null);
        String error = REQUIRED_REQUEST_BODY_IS_MISSING;
        when(walletService.update(walletOperationDto))
                .thenThrow(new WalletOperationException(error));

        RequestBuilder requestBuilder = put(POST_PATH)
                .content(mapper.writeValueAsString(walletOperationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ErrorMessage errorMessage = new ErrorMessage(BAD_REQUEST.value(), error, ZonedDateTime.now());
        ResultMatcher[] resultMatchers = {
                jsonPath("$.status").value(errorMessage.status()),
                jsonPath("$.error").value(errorMessage.error()),
                jsonPath("$.dateTime").exists()
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/wallet 400:BAD_REQUEST - null operation type json")
    void update_whenNullOperationTypeJson_then400() throws Exception {
        walletOperationDto.setOperationType(null);
        String error = REQUIRED_REQUEST_BODY_IS_MISSING;
        when(walletService.update(walletOperationDto))
                .thenThrow(new WalletOperationException(error));

        RequestBuilder requestBuilder = put(POST_PATH)
                .content(mapper.writeValueAsString(walletOperationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ErrorMessage errorMessage = new ErrorMessage(BAD_REQUEST.value(), error, ZonedDateTime.now());
        ResultMatcher[] resultMatchers = {
                jsonPath("$.status").value(errorMessage.status()),
                jsonPath("$.error").value(errorMessage.error()),
                jsonPath("$.dateTime").exists()
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/wallet 400:BAD_REQUEST - null operation type json")
    void update_whenNullAmountJson_then400() throws Exception {
        walletOperationDto.setAmount(null);
        String error = REQUIRED_REQUEST_BODY_IS_MISSING;
        when(walletService.update(walletOperationDto))
                .thenThrow(new WalletOperationException(error));

        RequestBuilder requestBuilder = put(POST_PATH)
                .content(mapper.writeValueAsString(walletOperationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ErrorMessage errorMessage = new ErrorMessage(BAD_REQUEST.value(), error, ZonedDateTime.now());
        ResultMatcher[] resultMatchers = {
                jsonPath("$.status").value(errorMessage.status()),
                jsonPath("$.error").value(errorMessage.error()),
                jsonPath("$.dateTime").exists()
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/wallet 400:BAD_REQUEST - insufficient funds.")
    void update_whenInsufficientFunds_then400status() throws Exception {
        walletOperationDto.setOperationType(WITHDRAW.name());
        String error = format("Insufficient funds. %d is to much.", amount);

        when(walletService.update(walletOperationDto))
                .thenThrow(new WalletBalancePaymentsException(error));

        RequestBuilder requestBuilder = put(PUT_PATH)
                .content(mapper.writeValueAsString(walletOperationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ErrorMessage errorMessage = new ErrorMessage(BAD_REQUEST.value(), error, ZonedDateTime.now());
        ResultMatcher[] resultMatchers = {
                jsonPath("$.status").value(errorMessage.status()),
                jsonPath("$.error").value(errorMessage.error()),
                jsonPath("$.dateTime").exists()
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/wallet 200:OK")
    void update_whenOK_then200OK() throws Exception {
        walletOperationDto.setOperationType(DEPOSIT.name());
        int balance = walletDto.getBalance();
        amount = RANDOM.nextInt(balance);
        walletOperationDto.setAmount(amount);
        walletDto.setBalance(balance - amount);

        when(walletService.update(walletOperationDto))
                .thenReturn(walletDto);

        RequestBuilder requestBuilder = put(PUT_PATH)
                .content(mapper.writeValueAsString(walletOperationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ResultMatcher[] resultMatchers = {
                jsonPath("$.walletId").value(walletDto.getWalletId().toString()),
                jsonPath("$.balance").value(walletDto.getBalance())
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/wallets/{uuid} 200:OK")
    void get_whenExistsWallet_thenOK() throws Exception {
        when(walletService.get(WALLET_UUID))
                .thenReturn(walletDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(GET_PATH, WALLET_UUID);

        ResultMatcher[] resultMatchers = {
                jsonPath("$.walletId").exists(),
                jsonPath("$.balance").value(walletDto.getBalance())
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/wallets/{uuid} 404:NOT_FOUND")
    void get_whenNotExistsWallet_then404() throws Exception {
        String error = "Wallet not found";

        when(walletService.get(WALLET_UUID))
                .thenThrow(new WalletNotFoundException(error));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(GET_PATH, WALLET_UUID);

        ErrorMessage errorMessage = new ErrorMessage(NOT_FOUND.value(), error, ZonedDateTime.now());
        ResultMatcher[] resultMatchers = {
                jsonPath("$.status").value(errorMessage.status()),
                jsonPath("$.error").value(errorMessage.error()),
                jsonPath("$.dateTime").exists()
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/wallets/{uuid} 404:NOT_FOUND - null walletId - json")
    void get_whenNullWalletIdJson_then404() throws Exception {
        String error = REQUIRED_REQUEST_BODY_IS_MISSING;

        when(walletService.get(WALLET_UUID))
                .thenThrow(new WalletNotFoundException(error));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(GET_PATH, WALLET_UUID);

        ErrorMessage errorMessage = new ErrorMessage(NOT_FOUND.value(), error, ZonedDateTime.now());
        ResultMatcher[] resultMatchers = {
                jsonPath("$.status").value(errorMessage.status()),
                jsonPath("$.error").value(errorMessage.error()),
                jsonPath("$.dateTime").exists()
        };

        mvc.perform(requestBuilder)
                .andExpectAll(resultMatchers)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/wallets/{id} 200:OK")
    void delete_whenExists_thenDelete() throws Exception {
        UUID uuid = UUID.randomUUID();
        doNothing().when(walletService).delete(uuid);

        mvc.perform(delete(DELETE_PATH, uuid.toString()))
                .andExpect(status().isOk());

        verify(walletService, times(1)).delete(uuid);
    }


    @Test
    @DisplayName("DELETE /api/v1/wallets/{id} 400:OK null walletId - json")
    void delete_whenNullWalletId_then400() throws Exception {
        doNothing().when(walletService).delete(null);

        ErrorMessage errorMessage = new ErrorMessage(
                BAD_REQUEST.value(),
                "No static resource api/v1/wallets.",
                ZonedDateTime.now()
        );
        ResultMatcher[] resultMatchers = {
                jsonPath("$.status").value(errorMessage.status()),
                jsonPath("$.error").value(errorMessage.error()),
                jsonPath("$.dateTime").exists()
        };

        mvc.perform(delete(DELETE_PATH, (Object) null))
                .andExpectAll(resultMatchers)
                .andExpect(status().isBadRequest());

        verify(walletService, never()).delete(null);
    }
}