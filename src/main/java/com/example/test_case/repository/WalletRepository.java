package com.example.test_case.repository;

import com.example.test_case.model.Wallet;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    @Override
    boolean existsById(@NonNull UUID uuid);

    @Override
    void deleteById(@NonNull UUID uuid);
}
