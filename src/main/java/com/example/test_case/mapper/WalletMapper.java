package com.example.test_case.mapper;

import com.example.test_case.dto.WalletDto;
import com.example.test_case.model.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, injectionStrategy = CONSTRUCTOR)
public interface WalletMapper {
    @Mapping(target = "walletId", source = "id")
    @Mapping(target = "amount", source = "balance")
    WalletDto toDto(Wallet wallet);

    @Mapping(target = "id", source = "walletId")
    @Mapping(target = "balance", source = "amount")
    Wallet toEntityFromDto(WalletDto walletDto);
}
