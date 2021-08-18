package com.example.ewalletapplication.repository;

import com.example.ewalletapplication.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.criteria.CriteriaBuilder;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    Wallet findByUserId(String userId);
}