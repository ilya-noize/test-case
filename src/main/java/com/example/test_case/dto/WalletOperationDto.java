package com.example.test_case.dto;

import com.example.test_case.model.OperationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletOperationDto {
    UUID walletId;
    @NotNull
    OperationType operationType;
    @Positive
    int amount;
}
