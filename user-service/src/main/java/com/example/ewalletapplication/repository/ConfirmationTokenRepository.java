package com.example.ewalletapplication.repository;

import com.example.ewalletapplication.model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken,Integer> {
    ConfirmationToken findByToken(String token);
}
